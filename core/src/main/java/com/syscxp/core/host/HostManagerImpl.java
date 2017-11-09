package com.syscxp.core.host;

import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.*;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.*;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.*;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.host.*;
import com.syscxp.header.managementnode.ManagementNodeChangeListener;
import com.syscxp.header.managementnode.ManagementNodeReadyExtensionPoint;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;
import com.syscxp.utils.Bucket;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.ObjectUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.function.ForEachFunction;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Tuple;
import java.util.*;

import static com.syscxp.core.Platform.argerr;

public class HostManagerImpl extends AbstractService implements HostManager, ManagementNodeChangeListener,
        ManagementNodeReadyExtensionPoint {
    private static final CLogger logger = Utils.getLogger(HostManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private HostExtensionPointEmitter extEmitter;
    @Autowired
    protected HostTracker tracker;
    @Autowired
    private EventFacade evtf;

    private Map<Class, HostBaseExtensionFactory> hostBaseExtensionFactories = new HashMap<>();


    private Map<String, HostFactory> hostFactoryMap = Collections.synchronizedMap(new HashMap<String, HostFactory>());
    private static final Set<Class> allowedMessageAfterSoftDeletion = new HashSet<>();

    static {
        allowedMessageAfterSoftDeletion.add(HostDeletionMsg.class);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIAddHostMsg) {
            handle((APIAddHostMsg) msg);
        } else if (msg instanceof APIGetHostMsg) {
            handle((APIGetHostMsg) msg);
        } else if (msg instanceof APIGetHostTypesMsg) {
            handle((APIGetHostTypesMsg) msg);
        } else if (msg instanceof HostMessage) {
            HostMessage hmsg = (HostMessage) msg;
            passThrough(hmsg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetHostTypesMsg msg) {
        APIGetHostTypesReply reply = new APIGetHostTypesReply();
        List<String> res = new ArrayList<>();
        res.addAll(HostType.getAllTypeNames());
        reply.setHostTypes(res);
        bus.reply(msg, reply);
    }

    private void handle(APIGetHostMsg msg) {
        HostVO host = Q.New(HostVO.class).eq(HostVO_.uuid, msg.getUuid()).find();

        HostInventory inv = getHostFactory(HostType.valueOf(host.getHostType())).getHostInventory(host);

        APIGetHostReply reply = new APIGetHostReply();
        reply.setInventory(inv);
        bus.reply(msg, reply);
    }


    private void passThrough(HostMessage msg) {
        HostVO vo = dbf.findByUuid(msg.getHostUuid(), HostVO.class);
        if (vo == null && allowedMessageAfterSoftDeletion.contains(msg.getClass())) {
            HostEO eo = dbf.findByUuid(msg.getHostUuid(), HostEO.class);
            vo = ObjectUtils.newAndCopy(eo, HostVO.class);
        }

        if (vo == null) {
            String err = "Cannot find host: " + msg.getHostUuid() + ", it may have been deleted";
            bus.replyErrorByMessageType((Message) msg, err);
            return;
        }

        HostFactory factory = this.getHostFactory(HostType.valueOf(vo.getHostType()));
        Host host = factory.getHost(vo);
        host.handleMessage((Message) msg);
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        if (msg instanceof HostMessage) {
            passThrough((HostMessage) msg);
        } else if (msg instanceof AddHostMsg){
            handle((AddHostMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private AddHostMsg getAddHostMsg(AddHostMessage msg) {
        if (msg instanceof AddHostMsg) {
            return (AddHostMsg) msg;
        } else if (msg instanceof APIAddHostMsg) {
            return AddHostMsg.valueOf((APIAddHostMsg) msg);
        }

        throw new CloudRuntimeException("unexpected addHost message: " + msg);
    }

    private void doAddHostInQueue(final AddHostMessage msg, ReturnValueCompletion<HostInventory> completion) {
        thdf.chainSubmit(new ChainTask(completion) {
            @Override
            public String getSyncSignature() {
                return String.format("add-host-%s", msg.getHostIp());
            }

            @Override
            public void run(final SyncTaskChain chain) {
                doAddHost(msg, new ReturnValueCompletion<HostInventory>(completion, chain) {
                    @Override
                    public void success(HostInventory returnValue) {
                        completion.success(returnValue);
                        chain.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        completion.fail(errorCode);
                        chain.next();
                    }
                });
            }

            @Override
            public String getName() {
                return getSyncSignature();
            }
        });
    }

    private void doAddHost(final AddHostMessage msg, ReturnValueCompletion<HostInventory > completion) {
        if (Q.New(HostVO.class).eq(HostVO_.hostIp, msg.getHostIp()).isExists()) {
            completion.fail(argerr("there has been a host having managementIp[%s]", msg.getHostIp()));
            return;
        }

        final HostVO hvo = new HostVO();
        if (msg.getResourceUuid() != null) {
            hvo.setUuid(msg.getResourceUuid());
        } else {
            hvo.setUuid(Platform.getUuid());
        }
        hvo.setName(msg.getName());
        hvo.setCode(msg.getCode());
        hvo.setHostType(msg.getHostType());
        hvo.setHostIp(msg.getHostIp());
        hvo.setPosition(msg.getPosition());
        hvo.setStatus(HostStatus.Connecting);
        hvo.setState(HostState.Enabled);

        final HostFactory factory = getHostFactory(HostType.valueOf(msg.getHostType()));
        final HostVO vo = factory.createHost(hvo, msg);
        final AddHostMsg amsg = getAddHostMsg(msg);

        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        final HostInventory inv = HostInventory.valueOf(vo);
        chain.setName(String.format("add-host-%s", vo.getUuid()));
        chain.then(new NoRollbackFlow() {
            String __name__ = "call-before-add-host-extension";

            private void callPlugins(final Iterator<HostAddExtensionPoint> it, final FlowTrigger trigger) {
                if (!it.hasNext()) {
                    trigger.next();
                    return;
                }

                HostAddExtensionPoint ext = it.next();
                ext.beforeAddHost(inv, new Completion(trigger) {
                    @Override
                    public void success() {
                        callPlugins(it, trigger);
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                List<HostAddExtensionPoint> exts = pluginRgty.getExtensionList(HostAddExtensionPoint.class);
                callPlugins(exts.iterator(), trigger);
            }

        }).then(new NoRollbackFlow() {
            String __name__ = "send-connect-host-message";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                ConnectHostMsg connectMsg = new ConnectHostMsg(vo.getUuid());
                connectMsg.setNewAdd(true);
                connectMsg.setStartPingTaskOnFailure(false);
//                bus.makeTargetServiceIdByResourceUuid(connectMsg, HostConstant.SERVICE_ID, hvo.getUuid());
                bus.makeLocalServiceId(connectMsg, HostConstant.SERVICE_ID);
                bus.send(connectMsg, new CloudBusCallBack(trigger) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            trigger.next();
                        } else {
                            trigger.fail(reply.getError());
                        }
                    }
                });
            }
        }).then(new NoRollbackFlow() {
            String __name__ = "call-after-add-host-extension";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                extEmitter.afterAddHost(inv, new Completion(trigger) {
                    @Override
                    public void success() {
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }
        }).done(new FlowDoneHandler(amsg) {
            @Override
            public void handle(Map data) {
                HostInventory inv = factory.getHostInventory(vo.getUuid());
                logger.debug(String.format("successfully added host[name:%s, hostType:%s, uuid:%s]", vo.getName(), vo.getHostType(), vo.getUuid()));
                completion.success(inv);

            }
        }).error(new FlowErrorHandler(amsg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                // delete host totally through the database, so other tables
                // refer to the host table will clean up themselves
                HostVO nvo = dbf.reload(vo);
                dbf.remove(nvo);
                dbf.eoCleanup(HostVO.class);
                HostInventory inv = HostInventory.valueOf(nvo);

                CollectionUtils.safeForEach(pluginRgty.getExtensionList(FailToAddHostExtensionPoint.class), new ForEachFunction<FailToAddHostExtensionPoint>() {
                    @Override
                    public void run(FailToAddHostExtensionPoint ext) {
                        ext.failedToAddHost(inv, msg);
                    }
                });

                completion.fail(errCode);
            }
        }).start();

    }

    @Deferred
    private void handle(final AddHostMsg msg) {
        final AddHostReply reply = new AddHostReply();

        doAddHostInQueue(msg, new ReturnValueCompletion<HostInventory>(msg) {
            @Override
            public void success(HostInventory returnValue) {
                reply.setInventory(returnValue);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    @Deferred
    private void handle(final APIAddHostMsg msg) {
        final APIAddHostEvent evt = new APIAddHostEvent(msg.getId());

        doAddHostInQueue(msg, new ReturnValueCompletion<HostInventory>(msg) {
            @Override
            public void success(HostInventory inventory) {
                evt.setInventory(inventory);
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(HostConstant.SERVICE_ID);
    }

    private void populateExtensions() {
        for (HostFactory f : pluginRgty.getExtensionList(HostFactory.class)) {
            HostFactory old = hostFactoryMap.get(f.getHostType().toString());
            if (old != null) {
                throw new CloudRuntimeException(String.format("duplicate HypervisorFactory[%s, %s] for hypervisor type[%s]",
                        old.getClass().getName(), f.getClass().getName(), f.getHostType()));
            }
            hostFactoryMap.put(f.getHostType().toString(), f);
        }

        for (HostBaseExtensionFactory ext : pluginRgty.getExtensionList(HostBaseExtensionFactory.class)) {
            for (Class clz : ext.getMessageClasses()) {
                HostBaseExtensionFactory old = hostBaseExtensionFactories.get(clz);
                if (old != null) {
                    throw new CloudRuntimeException(String.format("duplicate HostBaseExtensionFactory[%s, %s] for the" +
                            " message[%s]", old.getClass(), ext.getClass(), clz));
                }
                hostBaseExtensionFactories.put(clz, ext);
            }
        }

    }

    @Override
    public boolean start() {
        populateExtensions();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void nodeJoin(String nodeId) {
    }

    @Override
    @SyncThread
    public void nodeLeft(String nodeId) {
        logger.debug(String.format("Management node[uuid:%s] left, node[uuid:%s] starts to take over hosts", nodeId, Platform.getManagementServerId()));
        loadHost();
    }

    @Override
    public void iAmDead(String nodeId) {
    }

    private Bucket getHostManagedByUs() {
        int qun = 10000;
        long amount = dbf.count(HostVO.class);
        int times = (int) (amount / qun) + (amount % qun != 0 ? 1 : 0);
        List<String> connected = new ArrayList<String>();
        List<String> disconnected = new ArrayList<String>();
        int start = 0;
        for (int i = 0; i < times; i++) {
            SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
            q.select(HostVO_.uuid, HostVO_.status);
            q.setLimit(qun);
            q.setStart(start);
            List<Tuple> lst = q.listTuple();
            start += qun;
            for (Tuple t : lst) {
                String huuid = t.get(0, String.class);
                if (!destMaker.isManagedByUs(huuid)) {
                    continue;
                }
                HostStatus state = t.get(1, HostStatus.class);
                if (state == HostStatus.Connected) {
                    connected.add(huuid);
                } else {
                    // for Disconnected and Connecting, treat as Disconnected
                    disconnected.add(huuid);
                }
            }
        }

        return Bucket.newBucket(connected, disconnected);
    }


    private void loadHost() {
        Bucket hosts = getHostManagedByUs();
        List<String> connected = hosts.get(0);
        List<String> disconnected = hosts.get(1);
        List<String> hostsToLoad = new ArrayList<>();

        if (CoreGlobalProperty.UNIT_TEST_ON) {
            hostsToLoad.addAll(connected);
            hostsToLoad.addAll(disconnected);
        } else {
            if (HostGlobalProperty.RECONNECT_ALL_ON_BOOT) {
                hostsToLoad.addAll(connected);
                hostsToLoad.addAll(disconnected);
            } else {
                hostsToLoad.addAll(disconnected);
                tracker.trackHost(connected);
            }
        }

        if (hostsToLoad.isEmpty()) {
            return;
        }

        String serviceId = bus.makeLocalServiceId(HostConstant.SERVICE_ID);
        final List<ConnectHostMsg> msgs = new ArrayList<>(hostsToLoad.size());
        for (String uuid : hostsToLoad) {
            ConnectHostMsg connectMsg = new ConnectHostMsg(uuid);
            connectMsg.setNewAdd(false);
            connectMsg.setServiceId(serviceId);
            connectMsg.setStartPingTaskOnFailure(true);
            msgs.add(connectMsg);
        }

        bus.send(msgs, HostGlobalProperty.HOST_LOAD_PARALLELISM_DEGREE,
                new CloudBusSteppingCallback(null) {
            @Override
            public void run(NeedReplyMessage msg, MessageReply reply) {
                ConnectHostMsg cmsg = (ConnectHostMsg) msg;
                if (!reply.isSuccess()) {
                    logger.warn(String.format("failed to load host[uuid:%s], %s", cmsg.getHostUuid(), reply.getError()));
                } else {
                    logger.debug(String.format("host[uuid:%s] load successfully", cmsg.getHostUuid()));
                }
            }
        });
    }

    @Override
    public void iJoin(String nodeId) {
    }


    public HostFactory getHostFactory(HostType type) {
        HostFactory factory = hostFactoryMap.get(type.toString());
        if (factory == null) {
            throw new CloudRuntimeException("No factory for hypervisor: " + type + " found, check your HypervisorManager.xml");
        }

        return factory;
    }

    @Override
    @AsyncThread
    public void managementNodeReady() {
        logger.debug(String.format("Management node[uuid:%s] joins, start loading host...", Platform.getManagementServerId()));
        loadHost();
    }

    @Override
    public HostBaseExtensionFactory getHostBaseExtensionFactory(Message msg) {
        return hostBaseExtensionFactories.get(msg.getClass());
    }
}
