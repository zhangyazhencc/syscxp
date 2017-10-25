package com.syscxp.tunnel.header.host;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.ansible.AnsibleFacade;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusSteppingCallback;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.host.HostGlobalConfig;
import com.syscxp.core.notification.N;
import com.syscxp.core.thread.AsyncThread;
import com.syscxp.header.AbstractService;
import com.syscxp.header.Component;
import com.syscxp.header.host.*;
import com.syscxp.header.host.HostInventory;
import com.syscxp.header.host.HostStatus;
import com.syscxp.header.host.HostVO;
import com.syscxp.header.host.HostVO_;
import com.syscxp.header.managementnode.ManagementNodeReadyExtensionPoint;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.tunnel.header.host.MonitorAgentCommands.*;
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class MonitorHostFactory extends AbstractService implements HostFactory, Component,
        ManagementNodeReadyExtensionPoint {
    private static final CLogger logger = Utils.getLogger(MonitorHostFactory.class);

    public static final HostType hostType = new HostType(MonitorConstant.HOST_TYPE);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private AnsibleFacade asf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private CloudBus bus;
    @Autowired
    private RESTFacade restf;

    @Override
    public HostVO createHost(HostVO vo, AddHostMessage msg) {
        APICreateMonitorHostMsg amsg = (APICreateMonitorHostMsg) msg;
        MonitorHostVO host = new MonitorHostVO(vo);
        host.setNodeUuid(amsg.getNodeUuid());
        host.setUsername(amsg.getUsername());
        host.setPassword(amsg.getPassword());
        host.setSshPort(amsg.getSshPort());
        host = dbf.persistAndRefresh(host);
        return host;
    }

    @Override
    public Host getHost(HostVO vo) {
        MonitorHostVO host = dbf.findByUuid(vo.getUuid(), MonitorHostVO.class);
        MonitorHostContext context = getHostContext(vo.getUuid());
        if (context == null) {
            context = createHostContext(host);
        }
        return new MonitorHost(host, context);
    }

    @Override
    public HostType getHostType() {
        return hostType;
    }

    private List<String> getHostManagedByUs() {
        int qun = 10000;
        long amount = dbf.count(HostVO.class);
        int times = (int) (amount / qun) + (amount % qun != 0 ? 1 : 0);
        List<String> hostUuids = new ArrayList<String>();
        int start = 0;
        for (int i = 0; i < times; i++) {
            SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
            q.select(HostVO_.uuid);
            // disconnected host will be handled by HostManager
            q.add(HostVO_.status, SimpleQuery.Op.EQ, HostStatus.Connected);
            q.setLimit(qun);
            q.setStart(start);
            List<String> lst = q.listValue();
            start += qun;
            for (String huuid : lst) {
                if (!destMaker.isManagedByUs(huuid)) {
                    continue;
                }
                hostUuids.add(huuid);
            }
        }

        return hostUuids;
    }

    @Override
    public HostInventory getHostInventory(HostVO vo) {
        MonitorHostVO host = vo instanceof MonitorHostVO ? (MonitorHostVO) vo : dbf.findByUuid(vo.getUuid(), MonitorHostVO.class);
        return HostInventory.valueOf(host);
    }

    @Override
    public HostInventory getHostInventory(String uuid) {
        HostVO vo = dbf.findByUuid(uuid, HostVO.class);
        return vo == null ? null : HostInventory.valueOf(vo);
    }

    private void deployAnsibleModule() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        asf.deployModule(MonitorConstant.ANSIBLE_MODULE_PATH, MonitorConstant.ANSIBLE_PLAYBOOK_NAME);
    }

    @Override
    public boolean start() {
        deployAnsibleModule();

        restf.registerSyncHttpCallHandler(MonitorConstant.AGENT_RECONNECT_ME, ReconnectMeCmd.class, new SyncHttpCallHandler<ReconnectMeCmd>() {
            @Override
            public String handleSyncHttpCall(MonitorAgentCommands.ReconnectMeCmd cmd) {
                N.New(HostVO.class, cmd.hostUuid).info_("the kvm host[uuid:%s] asks the management server to reconnect it for %s", cmd.hostUuid, cmd.reason);
                ReconnectHostMsg msg = new ReconnectHostMsg();
                msg.setHostUuid(cmd.hostUuid);
                bus.makeTargetServiceIdByResourceUuid(msg, MonitorConstant.SERVICE_ID, cmd.hostUuid);
                bus.send(msg);
                return null;
            }
        });

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    public MonitorHostContext createHostContext(MonitorHostVO vo) {
        UriComponentsBuilder ub = UriComponentsBuilder.newInstance();
        ub.scheme(MonitorGlobalProperty.AGENT_URL_SCHEME);
        ub.host(vo.getHostIp());
        ub.port(MonitorGlobalProperty.AGENT_PORT);
        if (!"".equals(MonitorGlobalProperty.AGENT_URL_ROOT_PATH)) {
            ub.path(MonitorGlobalProperty.AGENT_URL_ROOT_PATH);
        }
        String baseUrl = ub.build().toUriString();

        MonitorHostContext context = new MonitorHostContext();
        context.setInventory(MonitorHostInventory.valueOf(vo));
        context.setBaseUrl(baseUrl);
        return context;
    }

    public MonitorHostContext getHostContext(String hostUuid) {
        MonitorHostVO host = dbf.findByUuid(hostUuid, MonitorHostVO.class);
        return createHostContext(host);
    }

    @Override
    @AsyncThread
    public void managementNodeReady() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        if (!asf.isModuleChanged(MonitorConstant.ANSIBLE_PLAYBOOK_NAME)) {
            return;
        }

        // hosts need to deploy new agent
        // connect hosts even if they are ConnectionState is Connected

        List<String> hostUuids = getHostManagedByUs();
        if (hostUuids.isEmpty()) {
            return;
        }

        logger.debug(String.format("need to connect hosts because kvm agent changed, uuids:%s", hostUuids));

        List<ConnectHostMsg> msgs = new ArrayList<>();
        for (String huuid : hostUuids) {
            ConnectHostMsg msg = new ConnectHostMsg();
            msg.setNewAdd(false);
            msg.setUuid(huuid);
            bus.makeTargetServiceIdByResourceUuid(msg, MonitorConstant.SERVICE_ID, huuid);
            msgs.add(msg);
        }

        bus.send(msgs, HostGlobalConfig.HOST_LOAD_PARALLELISM_DEGREE.value(Integer.class), new CloudBusSteppingCallback(null) {
            @Override
            public void run(NeedReplyMessage msg, MessageReply reply) {
                ConnectHostMsg cmsg = (ConnectHostMsg) msg;
                if (!reply.isSuccess()) {
                    logger.warn(String.format("failed to connect host[uuid:%s], %s", cmsg.getHostUuid(), reply.getError()));
                } else {
                    logger.debug(String.format("successfully to connect kvm host[uuid:%s]", cmsg.getHostUuid()));
                }
            }
        });
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {

        bus.dealWithUnknownMessage(msg);

    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(MonitorConstant.SERVICE_ID);
    }
}
