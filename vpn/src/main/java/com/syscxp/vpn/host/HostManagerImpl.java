package com.syscxp.vpn.host;

import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.vpn.VpnAgentResponse;
import com.syscxp.header.vpn.VpnAgentResponse.TaskResult;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.vpn.header.host.*;
import com.syscxp.vpn.header.vpn.VpnStatus;
import com.syscxp.vpn.vpn.VpnCommands.*;
import com.syscxp.vpn.vpn.VpnGlobalConfig;
import com.syscxp.vpn.vpn.VpnRESTCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;


public class HostManagerImpl extends AbstractService implements HostManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(HostManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;


    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateVpnHostMsg) {
            handle((APICreateVpnHostMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostMsg) {
            handle((APIUpdateVpnHostMsg) msg);
        } else if (msg instanceof APIDeleteVpnHostMsg) {
            handle((APIDeleteVpnHostMsg) msg);
        } else if (msg instanceof APICreateHostInterfaceMsg) {
            handle((APICreateHostInterfaceMsg) msg);
        } else if (msg instanceof APICreateZoneMsg) {
            handle((APICreateZoneMsg) msg);
        } else if (msg instanceof APIDeleteHostInterfaceMsg) {
            handle((APIDeleteHostInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteZoneMsg) {
            handle((APIDeleteZoneMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostStateMsg) {
            handle((APIUpdateVpnHostStateMsg) msg);
        } else if (msg instanceof APIUpdateZoneMsg) {
            handle((APIUpdateZoneMsg) msg);
        } else if (msg instanceof APIReconnectVpnHostMsg) {
            handle((APIReconnectVpnHostMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    private void handle(APIReconnectVpnHostMsg msg) {
        APIReconnectVpnHostEvent evt = new APIReconnectVpnHostEvent(msg.getId());
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);

        ReconnectVpnHostCmd cmd = ReconnectVpnHostCmd.valueOf(host);
        new VpnRESTCaller().sendCommand(HostConstant.RECONNECT_HOST_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                host.setStatus(HostStatus.Connected);
                dbf.persistAndRefresh(host);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });
        bus.publish(evt);
    }


    private void handle(APIDeleteHostInterfaceMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), HostInterfaceVO.class);
        APIDeleteHostInterfaceEvent evt = new APIDeleteHostInterfaceEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIDeleteZoneMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), ZoneVO.class);
        APIDeleteZoneEvent evt = new APIDeleteZoneEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIUpdateZoneMsg msg) {
        ZoneVO zone = dbf.findByUuid(msg.getUuid(), ZoneVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            zone.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            zone.setDescription(msg.getDescription());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getNodeUuid())) {
            zone.setNodeUuid(msg.getNodeUuid());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getProvince())) {
            zone.setProvince(msg.getProvince());
            update = true;
        }
        if (update)
            zone = dbf.updateAndRefresh(zone);

        APIUpdateZoneEvent evt = new APIUpdateZoneEvent(msg.getId());
        evt.setInventory(ZoneInventory.valueOf(zone));
        bus.publish(evt);

    }

    private void handle(APIUpdateVpnHostStateMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);

        // todo 暂时未考虑物理机上的vpn状态
        host.setState(HostState.valueOf(msg.getState()));

        host = dbf.updateAndRefresh(host);
        APIUpdateVpnHostStateEvent evt = new APIUpdateVpnHostStateEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }

    private void handle(APICreateZoneMsg msg) {
        ZoneVO zone = new ZoneVO();
        zone.setUuid(Platform.getUuid());
        zone.setProvince(msg.getProvince());
        zone.setName(msg.getName());
        zone.setDescription(msg.getDescription());
        zone.setNodeUuid(msg.getNodeUuid());

        zone = dbf.persistAndRefresh(zone);
        APICreateZoneEvent evt = new APICreateZoneEvent(msg.getId());
        evt.setInventory(ZoneInventory.valueOf(zone));
        bus.publish(evt);
    }

    private void handle(APICreateHostInterfaceMsg msg) {
        HostInterfaceVO iface = new HostInterfaceVO();
        iface.setUuid(Platform.getUuid());
        iface.setName(msg.getName());
        iface.setHostUuid(msg.getHostUuid());
        iface.setEndpointUuid(msg.getEndpointUuid());
        iface.setInterfaceUuid(msg.getInterfaceUuid());

        iface = dbf.persistAndRefresh(iface);

        APICreateHostInterfaceEvent evt = new APICreateHostInterfaceEvent(msg.getId());
        evt.setInventory(HostInterfaceInventory.valueOf(iface));
        bus.publish(evt);
    }


    private void handle(APIDeleteVpnHostMsg msg) {
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);

        DeleteVpnHostCmd cmd = DeleteVpnHostCmd.valueOf(host);
        new VpnRESTCaller().sendCommand(HostConstant.Delete_HOST_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                dbf.remove(host);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);
    }

    private void handle(APIUpdateVpnHostMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            host.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            host.setDescription(msg.getDescription());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPublicInterface())) {
            host.setPublicInterface(msg.getPublicInterface());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPublicIp())) {
            host.setPublicIp(msg.getPublicIp());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getManageIp())) {
            host.setManageIp(msg.getManageIp());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getSshPort())) {
            host.setSshPort(msg.getSshPort());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getUsername())) {
            host.setUsername(msg.getUsername());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPassword())) {
            host.setPassword(msg.getPassword());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getZoneUuid())) {
            host.setZone(dbf.findByUuid(msg.getZoneUuid(), ZoneVO.class));
            update = true;
        }

        if (update)
            dbf.updateAndRefresh(host);

        APIUpdateVpnHostEvent evt = new APIUpdateVpnHostEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }


    @Transactional
    public void handle(APICreateVpnHostMsg msg) {
        APICreateVpnHostEvent evt = new APICreateVpnHostEvent(msg.getId());
        VpnHostVO host = new VpnHostVO();
        host.setUuid(Platform.getUuid());
        host.setName(msg.getName());
        host.setDescription(msg.getDescription());
        host.setPublicInterface(msg.getPublicInterface());
        host.setPublicIp(msg.getPublicIp());
        host.setZoneUuid(msg.getZoneUuid());
        host.setManageIp(msg.getManageIp());
        host.setSshPort(msg.getSshPort());
        host.setUsername(msg.getUsername());
        host.setPassword(msg.getPassword());
        host.setState(HostState.Enabled);
        host.setStatus(HostStatus.Connecting);


        AddVpnHostCmd cmd = AddVpnHostCmd.valueOf(host);

        new VpnRESTCaller().sendCommand(HostConstant.ADD_HOST_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                host.setStatus(HostStatus.Connected);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                host.setStatus(HostStatus.Disconnected);
                evt.setError(errorCode);
            }
        });

        evt.setInventory(VpnHostInventory.valueOf(dbf.persistAndRefresh(host)));
        bus.publish(evt);
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }


    public String getId() {
        return bus.makeLocalServiceId(HostConstant.SERVICE_ID);
    }

    private Future<Void> hostCheckThread;
    private int hostStatusCheckWorkerInterval;
    private List<String> disconnectedHosts = new ArrayList<>();

    private List<String> getDisconnectedHosts() {

        return Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.state, HostState.Enabled)
                .eq(VpnHostVO_.status, HostStatus.Disconnected)
                .select(VpnHostVO_.uuid)
                .listValues();
    }

    private void startFailureHostCopingThread() {
        hostCheckThread = thdf.submitPeriodicTask(new HostStatusCheckWorker());
        logger.debug(String.format("security group failureHostCopingThread starts[failureHostWorkerInterval: %ss]", hostStatusCheckWorkerInterval));
    }

    private void restartFailureHostCopingThread() {
        if (hostCheckThread != null) {
            hostCheckThread.cancel(true);
        }
        startFailureHostCopingThread();
    }

    private void prepareGlobalConfig() {
        hostStatusCheckWorkerInterval = VpnGlobalConfig.STATUS_CHECK_WORKER_INTERVAL.value(Integer.class);

        GlobalConfigUpdateExtensionPoint onUpdate = new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                if (VpnGlobalConfig.STATUS_CHECK_WORKER_INTERVAL.isMe(newConfig)) {
                    hostStatusCheckWorkerInterval = newConfig.value(Integer.class);
                    restartFailureHostCopingThread();
                }
            }
        };

        VpnGlobalConfig.STATUS_CHECK_WORKER_INTERVAL.installUpdateExtension(onUpdate);
    }

    private class HostStatusCheckWorker implements PeriodicTask {
        @Transactional
        private List<VpnHostVO> getAllHosts() {

            return Q.New(VpnHostVO.class)
                    .eq(VpnHostVO_.state, HostState.Enabled)
                    .notEq(VpnHostVO_.status, HostStatus.Connecting)
                    .list();
        }

        private void updateHostStatus(List<String> disconnectedHosts) {
            logger.debug(String.format("update host status Disconnected, uuid in %s", JSONObjectUtil.toJsonString(disconnectedHosts)));
            UpdateQuery.New(VpnHostVO.class)
                    .in(VpnHostVO_.uuid, disconnectedHosts)
                    .set(VpnHostVO_.status, HostStatus.Disconnected)
                    .update();
        }

        // Host重连
        private boolean reconnectHost(VpnHostVO vo) {
            return false;
        }

        @Override
        public void run() {
            logger.debug("start check host status");
            disconnectedHosts.clear();
            List<VpnHostVO> vos = getAllHosts();
            if (vos.isEmpty()) {
                return;
            }
            for (VpnHostVO vo : vos) {
                if (vo.getStatus() == HostStatus.Disconnected){
                    disconnectedHosts.add(vo.getUuid());
                    continue;
                }
                CheckVpnHostStatusCmd cmd = CheckVpnHostStatusCmd.valueOf(vo);
                VpnAgentResponse rsp;
                try {
                    rsp = new VpnRESTCaller().syncPostForVPN(HostConstant.CHECK_HOST_STATUS_PATH, cmd);
                    if (rsp.getStatus() == VpnAgentResponse.RunStatus.UP)
                        continue;
                } catch (Exception ignored) {
                }
                if (!reconnectHost(vo))
                    disconnectedHosts.add(vo.getUuid());
            }

            updateHostStatus(disconnectedHosts);
        }


        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return hostStatusCheckWorkerInterval;
        }

        @Override
        public String getName() {
            return HostStatusCheckWorker.class.getName();
        }
    }


    public boolean start() {
        prepareGlobalConfig();
        return true;
    }


    public boolean stop() {
        return true;
    }

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnHostMsg) {
            validate((APICreateVpnHostMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostMsg) {
            validate((APIUpdateVpnHostMsg) msg);
        } else if (msg instanceof APICreateZoneMsg) {
            validate((APICreateZoneMsg) msg);
        } else if (msg instanceof APIDeleteZoneMsg) {
            validate((APIDeleteZoneMsg) msg);
        } else if (msg instanceof APIDeleteHostInterfaceMsg) {
            validate((APIDeleteHostInterfaceMsg) msg);
        }
        return msg;
    }


    private void validate(APIDeleteHostInterfaceMsg msg) {
    }

    private void validate(APIDeleteZoneMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.zoneUuid, msg.getUuid());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The Zone[uuid:%s] has at least one vpn host, can not delete.", msg.getUuid()
            ));
    }

    private void validate(APIDeleteVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.zoneUuid, msg.getUuid());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHostVO[uuid:%s] has at least one vpn instance, can not delete.", msg.getUuid()
            ));
    }

    private void validate(APICreateZoneMsg msg) {
        Q q = Q.New(ZoneVO.class)
                .eq(ZoneVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The ZoneVO[name:%s] is already exist.", msg.getName()
            ));
    }


    private void validate(APICreateVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
    }

    private void validate(APIUpdateVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
    }


}