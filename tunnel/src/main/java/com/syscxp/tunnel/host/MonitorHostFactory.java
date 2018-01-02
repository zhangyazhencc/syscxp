package com.syscxp.tunnel.host;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.ansible.AnsibleFacade;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusSteppingCallback;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.host.HostGlobalConfig;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.notification.N;
import com.syscxp.core.thread.AsyncThread;
import com.syscxp.header.AbstractService;
import com.syscxp.header.Component;
import com.syscxp.header.host.*;
import com.syscxp.header.managementnode.ManagementNodeReadyExtensionPoint;
import com.syscxp.header.message.*;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.host.*;
import com.syscxp.header.tunnel.monitor.TunnelMonitorVO;
import com.syscxp.header.tunnel.monitor.TunnelMonitorVO_;
import com.syscxp.header.tunnel.tunnel.TunnelVO;
import com.syscxp.tunnel.host.MonitorAgentCommands.ReconnectMeCmd;
import com.syscxp.tunnel.monitor.MonitorManagerImpl;
import com.syscxp.tunnel.tunnel.job.MonitorJobType;
import com.syscxp.tunnel.tunnel.job.TunnelMonitorJob;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class MonitorHostFactory extends AbstractService implements HostFactory, Component,
        ManagementNodeReadyExtensionPoint {
    private static final CLogger logger = Utils.getLogger(MonitorHostFactory.class);

    public static final HostType hostType = new HostType(MonitorHostConstant.HOST_TYPE);
    private List<MonitorHostConnectExtensionPoint> connectExtensions = new ArrayList<>();

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
    @Autowired
    private JobQueueFacade jobf;

    @Override
    public HostVO createHost(HostVO vo, AddHostMessage msg) {
        APICreateMonitorHostMsg amsg = (APICreateMonitorHostMsg) msg;
        MonitorHostVO host = new MonitorHostVO(vo);
        host.setNodeUuid(amsg.getNodeUuid());
        host.setUsername(amsg.getUsername());
        host.setPassword(amsg.getPassword());
        host.setSshPort(amsg.getSshPort());
        host.setMonitorType(amsg.getMonitorType());
        return dbf.persistAndRefresh(host);
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
        return MonitorHostInventory.valueOf(host);
    }

    @Override
    public HostInventory getHostInventory(String uuid) {
        MonitorHostVO vo = dbf.findByUuid(uuid, MonitorHostVO.class);
        return vo == null ? null : MonitorHostInventory.valueOf(vo);
    }

    private void deployAnsibleModule() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        asf.deployModule(MonitorHostConstant.ANSIBLE_MODULE_PATH, MonitorHostConstant.ANSIBLE_PLAYBOOK_NAME);
    }

    private void populateExtensions() {
        connectExtensions = pluginRgty.getExtensionList(MonitorHostConnectExtensionPoint.class);
    }

    public List<MonitorHostConnectExtensionPoint> getConnectExtensions() {
        return connectExtensions;
    }

    @Override
    public boolean start() {
        deployAnsibleModule();
        populateExtensions();

        restf.registerSyncHttpCallHandler(MonitorHostConstant.AGENT_RECONNECT_ME, ReconnectMeCmd.class,
                cmd -> {
                    N.New(HostVO.class, cmd.hostUuid).info_("the monitor host[uuid:%s] asks the management server to " +
                            "reconnect it for %s", cmd.hostUuid, cmd.reason);
                    ReconnectHostMsg msg = new ReconnectHostMsg();
                    msg.setHostUuid(cmd.hostUuid);
                    bus.makeLocalServiceId(msg, HostConstant.SERVICE_ID);
                    bus.send(msg);
                    return null;
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

        if (!asf.isModuleChanged(MonitorHostConstant.ANSIBLE_PLAYBOOK_NAME)) {
            return;
        }

        // hosts need to deploy new agent
        // connect hosts even if they are ConnectionState is Connected

        List<String> hostUuids = getHostManagedByUs();
        if (hostUuids.isEmpty()) {
            return;
        }

        logger.debug(String.format("need to connect hosts because agent changed, uuids:%s", hostUuids));

        List<ConnectHostMsg> msgs = new ArrayList<>();
        for (String huuid : hostUuids) {
            ConnectHostMsg msg = new ConnectHostMsg();
            msg.setNewAdd(false);
            msg.setUuid(huuid);
            bus.makeLocalServiceId(msg, HostConstant.SERVICE_ID);
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

    private void handle(APICreateHostSwitchMonitorMsg msg) {
        HostSwitchMonitorVO vo = new HostSwitchMonitorVO();

        vo.setUuid(Platform.getUuid());
        vo.setHostUuid(msg.getHostUuid());
        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
        vo.setInterfaceName(msg.getInterfaceName());

        vo = dbf.persistAndRefresh(vo);

        APICreateHostSwitchMonitorEvent event = new APICreateHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdateHostSwitchMonitorMsg msg) {
        HostSwitchMonitorVO vo = dbf.findByUuid(msg.getUuid(), HostSwitchMonitorVO.class);

        vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
        vo.setInterfaceName(msg.getInterfaceName());
        vo = dbf.updateAndRefresh(vo);

        // 更新所有受影响的监控通道（监控重启）
        List<TunnelMonitorVO> hostTunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.hostUuid, vo.getHostUuid())
                .list();

        APIUpdateHostSwitchMonitorEvent event = new APIUpdateHostSwitchMonitorEvent(msg.getId());
        for (TunnelMonitorVO hostTunnelMonitorVO : hostTunnelMonitorVOS) {
            TunnelVO tunnelVO = dbf.findByUuid(hostTunnelMonitorVO.getTunnelUuid(),TunnelVO.class);
            MonitorManagerImpl monitorManager = new MonitorManagerImpl();

            monitorManager.initTunnelMonitor(tunnelVO.getUuid(),tunnelVO.getMonitorCidr());
            try {
                monitorManager.modifyControllerMonitor(hostTunnelMonitorVO.getTunnelUuid());
                logger.info("修改监控成功：" + hostTunnelMonitorVO.getTunnelUuid());
            } catch (Exception e) {
                logger.error("修改监控失败，启动job: " + hostTunnelMonitorVO.getTunnelUuid() + " Error: " + e.getMessage());
                TunnelMonitorJob monitorJob = new TunnelMonitorJob();
                monitorJob.setTunnelUuid(hostTunnelMonitorVO.getTunnelUuid());
                monitorJob.setJobType(MonitorJobType.MODIFY);

                jobf.execute("修改监控接口-修改监控", Platform.getManagementServerId(), monitorJob);
            }
        }

        if (event.isSuccess())
            event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteHostSwitchMonitorMsg msg) {

        HostSwitchMonitorVO vo = dbf.findByUuid(msg.getUuid(), HostSwitchMonitorVO.class);

        dbf.remove(vo);

        APIDeleteHostSwitchMonitorEvent event = new APIDeleteHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));

        bus.publish(event);
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
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateHostSwitchMonitorMsg) {
            handle((APICreateHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIUpdateHostSwitchMonitorMsg) {
            handle((APIUpdateHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIDeleteHostSwitchMonitorMsg) {
            handle((APIDeleteHostSwitchMonitorMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(MonitorHostConstant.SERVICE_ID);
    }
}
