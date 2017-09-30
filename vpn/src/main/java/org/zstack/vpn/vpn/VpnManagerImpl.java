package org.zstack.vpn.vpn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigUpdateExtensionPoint;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.Q;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.UpdateQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.PeriodicTask;
import org.zstack.core.thread.Task;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.agent.OrderCallbackCmd;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.billing.*;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.AccountType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIReply;
import org.zstack.header.message.Message;
import org.zstack.header.query.QueryOp;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.SyncHttpCallHandler;
import org.zstack.header.vpn.VpnAgentCommand;
import org.zstack.header.vpn.VpnAgentResponse;
import org.zstack.utils.CollectionDSL;
import org.zstack.utils.URLBuilder;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.vpn.header.host.*;
import org.zstack.vpn.header.vpn.*;
import org.zstack.vpn.vpn.VpnCommands.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.zstack.core.Platform.argerr;


public class VpnManagerImpl extends AbstractService implements VpnManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(VpnManagerImpl.class);

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
        if (msg instanceof APICreateVpnMsg) {
            handle((APICreateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            handle((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBandwidthMsg) {
            handle((APIUpdateVpnBandwidthMsg) msg);
        } else if (msg instanceof APIDeleteVpnMsg) {
            handle((APIDeleteVpnMsg) msg);
        } else if (msg instanceof APICreateVpnInterfaceMsg) {
            handle((APICreateVpnInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteVpnInterfaceMsg) {
            handle((APIDeleteVpnInterfaceMsg) msg);
        } else if (msg instanceof APICreateVpnRouteMsg) {
            handle((APICreateVpnRouteMsg) msg);
        } else if (msg instanceof APIDeleteVpnRouteMsg) {
            handle((APIDeleteVpnRouteMsg) msg);
        } else if (msg instanceof APIGetVpnMsg) {
            handle((APIGetVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnStateMsg) {
            handle((APIUpdateVpnStateMsg) msg);
        } else if (msg instanceof APIUpdateVpnCidrMsg) {
            handle((APIUpdateVpnCidrMsg) msg);
        } else if (msg instanceof APIUpdateVpnExpireDateMsg) {
            handle((APIUpdateVpnExpireDateMsg) msg);
        } else if (msg instanceof APIDownloadCertificateMsg) {
            handle((APIDownloadCertificateMsg) msg);
        } else if (msg instanceof APIResetCertificateMsg) {
            handle((APIResetCertificateMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIResetCertificateMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(Platform.getUuid());

        ResetCertificateCmd cmd = ResetCertificateCmd.valueOf(vpn);
        ResetCertificateResponse rsp = new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.INIT_VPN_PATH, cmd, ResetCertificateResponse.class);

        dbf.updateAndRefresh(vpn);

        APIResetCertificateEvent evt = new APIResetCertificateEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);

    }

    private void handle(APIDownloadCertificateMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        DownloadCertificateCmd cmd = DownloadCertificateCmd.valueOf(vpn);
        DownloadCertificateResponse rsp = new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.DOWNLOAD_CERTIFICATE_PATH, cmd,
                        DownloadCertificateResponse.class);

        CertificateInventory inv = new CertificateInventory();
        inv.setCaCert(rsp.getCaCert());
        inv.setClientCert(rsp.getClientCert());
        inv.setClientConf(rsp.getClientConf());
        inv.setClientKey(rsp.getClientKey());
        APIDownloadCertificateEvent evt = new APIDownloadCertificateEvent(msg.getId());
        evt.setInventory(inv);
        bus.publish(evt);

    }

    private void handle(APIGetVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        VpnInventory inventory = VpnInventory.valueOf(vpn);

        APIGetVpnReply reply = new APIGetVpnReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);
    }

    public void handle(APICreateVpnMsg msg) {
        VpnVO vpn = new VpnVO();
        vpn.setUuid(Platform.getUuid());
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(Platform.getUuid());
        vpn.setMaxModifies(CoreGlobalProperty.VPN_MAX_MOTIFIES);
        vpn.setAccountUuid(msg.getAccountUuid());
        vpn.setDescription(msg.getDescription());
        vpn.setName(msg.getName());
        vpn.setVpnCidr(msg.getVpnCidr());
        vpn.setBandwidth(msg.getBandwidth());
        vpn.setEndpointUuid(msg.getEndpointUuid());
        vpn.setPayment(Payment.UNPAID);
        vpn.setState(VpnState.Creating);
        vpn.setStatus(VpnStatus.Connecting);
        vpn.setDuration(msg.getDuration());
        vpn.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));

        vpn.setPort(generatePort(msg.getHostUuid()));
        vpn.setHostUuid(msg.getHostUuid());
        vpn.setVpnHost(dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class));

        VpnInterfaceVO vpnIface = new VpnInterfaceVO();
        vpnIface.setUuid(Platform.getUuid());
        vpnIface.setName(msg.getName() + msg.getVlan());
        vpnIface.setVpnUuid(vpn.getUuid());
        vpnIface.setLocalIp(msg.getLocalIp());
        vpnIface.setNetmask(msg.getNetmask());
        vpnIface.setVlan(msg.getVlan());
        vpnIface.setNetworkUuid(msg.getNetworkUuid());
        vpn.setVpnInterfaces(CollectionDSL.list(vpnIface));

        vpn = dbf.persistAndRefresh(vpn);
        dbf.persistAndRefresh(vpnIface);

        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vpn.getName());
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        orderMsg.setDuration(vpn.getDuration());
        orderMsg.setProductDescription(vpn.getDescription());
        orderMsg.setUnits(msg.getProductPriceUnits());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        if (createOrder(orderMsg)) {
            vpn.setPayment(Payment.PAID);
            dbf.getEntityManager().merge(vpn);
        }

        CreateVpnCmd cmd = CreateVpnCmd.valueOf(vpn);
        CreateVpnResponse rsp = new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.INIT_VPN_PATH, cmd, CreateVpnResponse.class);
        if (rsp.getStatusCode() == HttpStatus.OK && rsp.getResult().isSuccess()) {
            checkVpnCreateState(cmd);
        }

        APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    private boolean createOrder(APICreateOrderMsg orderMsg) {
        APIReply rsp =
                new VpnRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);
        if (rsp.isSuccess())
            throw new CloudRuntimeException("测试");
        return rsp.isSuccess();
    }

    private void checkVpnCreateState(CreateVpnCmd cmd) {
        logger.debug("checkVpnCreateState");
        thdf.submit(new Task<Object>() {

            @Override
            public Object call() throws Exception {
                String url = URLBuilder.buildUrlFromBase(VpnConstant.CHECK_CREATE_STATE_PATH, cmd.getVpnUuid());
                CheckStatusResponse rsp =
                        new VpnRESTCaller().asyncCheckState(url, cmd);
                VpnVO vpnVO = dbf.findByUuid(cmd.getVpnUuid(), VpnVO.class);
                if (rsp.getResult().isSuccess()) {
                    vpnVO.setState(VpnState.Enabled);
                    vpnVO.setStatus(VpnStatus.Connected);
                } else {
                    vpnVO.setState(VpnState.Enabled);
                    vpnVO.setStatus(VpnStatus.Disconnected);
                    vpnVO.setMemo(rsp.getResult().getMessage());
                }

                dbf.updateAndRefresh(vpnVO);
                return null;
            }

            @Override
            public String getName() {
                return "checkVpnCreateState";
            }
        });
    }

    // 生成端口号，规则：从30000开始，当前主机存在的vpn服务端口号+1
    private Integer generatePort(String hostUuid) {
        Q q = Q.New(VpnVO.class).eq(VpnVO_.hostUuid, hostUuid)
                .orderBy(VpnVO_.port, SimpleQuery.Od.DESC).limit(1).select(VpnVO_.port);
        boolean flag = q.isExists();
        if (!flag)
            return 30000;
        return (Integer) q.findValue() + 1;
    }

    @Transactional
    public void handle(APIUpdateVpnExpireDateMsg msg) {

        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        LocalDateTime newTime = vpn.getExpireDate().toLocalDateTime();
        switch (msg.getType()) {
            case RENEW:
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
                renewOrderMsg.setProductUuid(vpn.getUuid());
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
                renewOrderMsg.setAccountUuid(msg.getAccountUuid());
                renewOrderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                renewOrderMsg.setStartTime(vpn.getCreateDate());
                renewOrderMsg.setExpiredTime(vpn.getExpireDate());
                createOrder(renewOrderMsg);
                newTime = newTime.plusMonths(msg.getDuration());
                break;
            case SLA_COMPENSATION:
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                        new APICreateSLACompensationOrderMsg();
                slaCompensationOrderMsg.setProductUuid(vpn.getUuid());
                slaCompensationOrderMsg.setProductName(vpn.getName());
                slaCompensationOrderMsg.setProductDescription(vpn.getDescription());
                slaCompensationOrderMsg.setProductType(ProductType.VPN);
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                slaCompensationOrderMsg.setStartTime(vpn.getCreateDate());
                slaCompensationOrderMsg.setExpiredTime(vpn.getExpireDate());
                createOrder(slaCompensationOrderMsg);
                newTime = newTime.plusDays(msg.getDuration());
                break;
            default:
                break;
        }
        vpn.setExpireDate(Timestamp.valueOf(newTime));

        vpn = dbf.getEntityManager().merge(vpn);
        APIUpdateVpnExpireDateEvent evt = new APIUpdateVpnExpireDateEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnBandwidthMsg msg) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        VpnMotifyRecordVO record = new VpnMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setVpnUuid(vpn.getUuid());
        record.setOpAccountUuid(msg.getOpAccountUuid());
        record.setMotifyType(
                msg.getBandwidth() > vpn.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DEMOTION);

        vpn.setBandwidth(msg.getBandwidth());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductName(vpn.getName());
        orderMsg.setProductDescription(vpn.getDescription());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setUnits(msg.getUnits());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());
        createOrder(orderMsg);
        vpn = dbf.getEntityManager().merge(vpn);
        dbf.getEntityManager().persist(record);

        UpdateVpnBandWidthCmd cmd = UpdateVpnBandWidthCmd.valueOf(vpn);
        UpdateVpnBandWidthResponse rsp = new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.UPDATE_VPN_BANDWIDTH_PATH, cmd, UpdateVpnBandWidthResponse.class);

        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnMsg msg) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            vpn.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            vpn.setDescription(msg.getDescription());
            update = true;
        }
        if (msg.getMaxModifies() != null) {
            vpn.setMaxModifies(msg.getMaxModifies());
            update = true;
        }
        if (update)
            vpn = dbf.getEntityManager().merge(vpn);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnStateMsg msg) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        vpn.setState(msg.getState());

        VpnAgentCommand cmd = null;
        String path = null;
        switch (msg.getState()) {
            case Enabled:
                cmd = CreateVpnCmd.valueOf(vpn);
                path = VpnConstant.START_VPN_PATH;
                break;
            case Disabled:
                cmd = StopVpnCmd.valueOf(vpn);
                path = VpnConstant.STOP_VPN_PATH;
                break;
        }
        if (cmd != null) {
            VpnAgentResponse rsp = new VpnRESTCaller().syncPostForVpn(path, cmd, VpnAgentResponse.class);
        }

        vpn = dbf.getEntityManager().merge(vpn);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnCidrMsg msg) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        vpn.setVpnCidr(msg.getVpnCidr());

        UpdateVpnCidrCmd cmd = UpdateVpnCidrCmd.valueOf(vpn);
        new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.UPDATE_VPN_CIDR_PATH, cmd, UpdateVpnCidrResponse.class);


        vpn = dbf.getEntityManager().merge(vpn);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }


    @Transactional
    public void handle(APIDeleteVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductName(vpn.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());
        createOrder(orderMsg);


        DeleteVpnCmd cmd = DeleteVpnCmd.valueOf(vpn);
        DeleteVpnResponse rsp = new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.DELETE_VPN_PATH, cmd, DeleteVpnResponse.class);

        dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        bus.publish(evt);
    }

    @Transactional
    public void handle(APICreateVpnInterfaceMsg msg) {
        VpnInterfaceVO iface = new VpnInterfaceVO();
        iface.setUuid(Platform.getUuid());
        iface.setVpnUuid(msg.getVpnUuid());
        iface.setName(msg.getName());
        iface.setNetworkUuid(msg.getTunnelUuid());
        iface.setLocalIp(msg.getLocalIP());
        iface.setNetmask(msg.getNetmask());

        VpnInterfaceCmd cmd = VpnInterfaceCmd.valueOf(msg.getLocalIP(), iface);
        new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.ADD_VPN_INTERFACE_PATH, cmd, VpnInterfaceResponse.class);


        iface = dbf.persistAndRefresh(iface);
        APICreateVpnInterfaceEvent evt = new APICreateVpnInterfaceEvent(msg.getId());
        evt.setInventory(VpnInterfaceInventory.valueOf(iface));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnInterfaceMsg msg) {
        VpnInterfaceVO iface = dbf.findByUuid(msg.getUuid(), VpnInterfaceVO.class);

        VpnVO vpn = dbf.findByUuid(iface.getVpnUuid(), VpnVO.class);
        VpnInterfaceCmd cmd = VpnInterfaceCmd.valueOf(vpn.getVpnHost().getManageIp(), iface);
        new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.DELETE_VPN_INTERFACE_PATH, cmd, VpnInterfaceResponse.class);

        dbf.removeByPrimaryKey(msg.getUuid(), VpnInterfaceVO.class);
        APIDeleteVpnInterfaceEvent evt = new APIDeleteVpnInterfaceEvent(msg.getId());
        bus.publish(evt);
    }

    @Transactional
    public void handle(APICreateVpnRouteMsg msg) {
        VpnRouteVO route = new VpnRouteVO();
        route.setVpnUuid(msg.getVpnUuid());
        route.setRouteType(msg.getRouteType());
        route.setNextInterface(msg.getNextIface());
        route.setTargetCidr(msg.getTargetCidr());

        VpnVO vpn = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        VpnRouteCmd cmd = VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);
        new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.ADD_VPN_ROUTE_PATH, cmd, VpnRouteResponse.class);

        route = dbf.persistAndRefresh(route);
        APICreateVpnRouteEvent evt = new APICreateVpnRouteEvent(msg.getId());
        evt.setInventory(VpnRouteInventory.valueOf(route));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnRouteMsg msg) {
        VpnRouteVO route = dbf.findByUuid(msg.getUuid(), VpnRouteVO.class);

        VpnVO vpn = dbf.findByUuid(route.getVpnUuid(), VpnVO.class);
        VpnRouteCmd cmd = VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);
        new VpnRESTCaller()
                .syncPostForVpn(VpnConstant.DELETE_VPN_ROUTE_PATH, cmd, VpnRouteResponse.class);

        dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
        APIDeleteVpnRouteEvent evt = new APIDeleteVpnRouteEvent(msg.getId());
        bus.publish(evt);
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }


    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.SERVICE_ID);
    }

    private Future<Void> hostCheckThread;
    private int vpnStatusCheckWorkerInterval;

    private void startFailureHostCopingThread() {
        hostCheckThread = thdf.submitPeriodicTask(new VpnStatusCheckWorker());
        logger.debug(String
                .format("security group failureHostCopingThread starts[failureHostWorkerInterval: %ss]",
                        vpnStatusCheckWorkerInterval));
    }

    private void restartFailureHostCopingThread() {
        if (hostCheckThread != null) {
            hostCheckThread.cancel(true);
        }
        startFailureHostCopingThread();
    }

    private void prepareGlobalConfig() {
        vpnStatusCheckWorkerInterval =
                VpnGlobalConfig.STATUS_CHECK_WORKER_INTERVAL.value(Integer.class);

        GlobalConfigUpdateExtensionPoint onUpdate = new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                if (VpnGlobalConfig.STATUS_CHECK_WORKER_INTERVAL.isMe(newConfig)) {
                    vpnStatusCheckWorkerInterval = newConfig.value(Integer.class);
                    restartFailureHostCopingThread();
                }
            }
        };

        VpnGlobalConfig.STATUS_CHECK_WORKER_INTERVAL.installUpdateExtension(onUpdate);
    }

    private class VpnStatusCheckWorker implements PeriodicTask {
        @Transactional
        private List<VpnVO> getAllVpns() {

            return Q.New(VpnVO.class).eq(VpnVO_.state, VpnState.Enabled).list();
        }

        private void updateVpnStatus(List<String> vpnUuids) {
            UpdateQuery.New(VpnVO.class).in(VpnVO_.uuid, vpnUuids)
                    .set(VpnVO_.status, VpnStatus.Disconnected).update();
        }

        private boolean reconnectVpn(VpnVO vpn) {
            if (vpn.getVpnHost().getStatus() == HostStatus.Disconnected) {
                return false;
            }
            //Todo VPN重连
            ReconnectVpnCmd cmd = ReconnectVpnCmd.valueOf(vpn);
            new VpnRESTCaller()
                    .syncPostForVpn(VpnConstant.RECONNECT_VPN_PATH, cmd, ReconnectVpnResponse.class);

            return true;
        }

        @Override
        public void run() {
            List<VpnVO> vos = getAllVpns();
            List<String> disconnectedVpn = new ArrayList<>();
            if (vos.isEmpty()) {
                return;
            }
            for (VpnVO vo : vos) {
                boolean flag = false;
                if (vo.getStatus() == VpnStatus.Disconnected) {
                    flag = reconnectVpn(vo);
                } else {
                    CheckVpnStatusCmd cmd = CheckVpnStatusCmd.valueOf(vo);
                    CheckStatusResponse rsp =
                            new VpnRESTCaller().checkState(VpnConstant.CHECK_VPN_STATUS_PATH, cmd);
                    if (rsp.getStatusCode() != HttpStatus.OK || rsp.getStatus() != RunStatus.UP) {
                        flag = reconnectVpn(vo);
                    }
                }
                if (flag)
                    disconnectedVpn.add(vo.getUuid());
            }
            updateVpnStatus(disconnectedVpn);
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return vpnStatusCheckWorkerInterval;
        }

        @Override
        public String getName() {
            return VpnStatusCheckWorker.class.getName();
        }
    }


    public boolean start() {
        prepareGlobalConfig();
        restf.registerSyncHttpCallHandler(ProductType.VPN.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        VpnVO vpn = dbf.findByUuid(cmd.getPorductUuid(), VpnVO.class);
                        if (vpn.getPayment() == Payment.UNPAID)
                            vpn.setPayment(Payment.PAID);
                        if (cmd.getExpireDate() != null)
                            vpn.setExpireDate(cmd.getExpireDate());
                        dbf.updateAndRefresh(vpn);
                        return null;
                    }
                });
        return true;
    }

    public boolean stop() {
        return true;
    }

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnMsg) {
            validate((APICreateVpnMsg) msg);
        } else if (msg instanceof APIQueryVpnMsg) {
            validate((APIQueryVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            validate((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBandwidthMsg) {
            validate((APIUpdateVpnBandwidthMsg) msg);
        } else if (msg instanceof APIDownloadCertificateMsg) {
            validate((APIDownloadCertificateMsg) msg);
        }
        return msg;
    }

    private void validate(APIDownloadCertificateMsg msg) {
        boolean exists =
                Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).eq(VpnVO_.sid, msg.getSid())
                        .eq(VpnVO_.certKey, msg.getKey()).isExists();
        if (!exists) {
            throw new ApiMessageInterceptionException(
                    argerr("Vpn[uuid:%s, sid:%s, key:%s]验证不通过.", msg.getUuid(), msg.getSid(),
                            msg.getKey()));
        }
    }

    private void validate(APIUpdateVpnBandwidthMsg msg) {
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(VpnMotifyRecordVO.class).eq(VpnMotifyRecordVO_.vpnUuid, msg.getUuid())
                .gte(VpnMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime))
                .lt(VpnMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime.plusMonths(1))).count();
        Integer maxModifies =
                Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).select(VpnVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[uuid:%s] has motified %s times.", msg.getUuid(), times));
        }
    }


    private void validate(APIUpdateVpnMsg msg) {
        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[name:%s] is already exist.", msg.getName()));
    }

    private void validate(APIQueryVpnMsg msg) {
        if (msg.getSession().getType() != AccountType.SystemAdmin) {
            msg.addQueryCondition(VpnVO_.accountUuid.toString(), QueryOp.EQ,
                    msg.getSession().getAccountUuid());
        }
    }

    private void validate(APICreateVpnMsg msg) {
        if (msg.getSession().getType() == AccountType.Normal && StringUtils
                .isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] is not a admin or proxy.",
                            msg.getSession().getAccountUuid()));
        }
        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[name:%s] is already exist.", msg.getName()));

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(msg.getProductPriceUnits());
        APIGetProductPriceReply rsp =
                (APIGetProductPriceReply) new VpnRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL)
                        .syncJsonPost(priceMsg);
        if (!rsp.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }
}
