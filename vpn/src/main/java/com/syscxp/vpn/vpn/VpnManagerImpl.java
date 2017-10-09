package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.Task;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.query.QueryOp;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.header.vpn.VpnAgentCommand;
import com.syscxp.header.vpn.VpnAgentResponse;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.header.host.APIDeleteVpnHostEvent;
import com.syscxp.vpn.header.host.HostStatus;
import com.syscxp.vpn.header.host.VpnHostVO;
import com.syscxp.vpn.header.vpn.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;


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
        APIResetCertificateEvent evt = new APIResetCertificateEvent(msg.getId());

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(Platform.getUuid());

        VpnCommands.ResetCertificateCmd cmd = VpnCommands.ResetCertificateCmd.valueOf(vpn);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.INIT_VPN_PATH, cmd);
        if (result.isSuccess()) {
            dbf.updateAndRefresh(vpn);
            evt.setInventory(VpnInventory.valueOf(vpn));
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }

        bus.publish(evt);

    }

    private void handle(APIDownloadCertificateMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        VpnCommands.DownloadCertificateCmd cmd = VpnCommands.DownloadCertificateCmd.valueOf(vpn);
        VpnCommands.DownloadCertificateResponse rsp = new VpnRESTCaller()
                .syncPost(VpnConstant.DOWNLOAD_CERTIFICATE_PATH, cmd,
                        VpnCommands.DownloadCertificateResponse.class);
        if (rsp.getStatusCode() != HttpStatus.OK){
            throw new OperationFailureException(operr("failed to post to %s, status code: %s, result: %s",
                    VpnConstant.DOWNLOAD_CERTIFICATE_PATH, rsp.getStatusCode(), rsp.getResult()));
        }
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
        APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());
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
        if (!createOrder(orderMsg)) {
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(VpnInventory.valueOf(vpn));
            bus.publish(evt);
            return;
        }
        vpn.setPayment(Payment.PAID);
        dbf.getEntityManager().merge(vpn);

        VpnCommands.CreateVpnCmd cmd = VpnCommands.CreateVpnCmd.valueOf(vpn);
        VpnCommands.CreateVpnResponse rsp = new VpnRESTCaller()
                .syncPost(VpnConstant.INIT_VPN_PATH, cmd, VpnCommands.CreateVpnResponse.class);
        if (rsp.getStatusCode() == HttpStatus.OK && rsp.getResult().isSuccess()) {
            checkVpnCreateState(cmd);
        } else {
            evt.setError(errf.stringToOperationError("VPN创建失败"));
        }

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

    private void checkVpnCreateState(VpnCommands.CreateVpnCmd cmd) {
        logger.debug("checkVpnCreateState");
        thdf.submit(new Task<Object>() {

            @Override
            public Object call() throws Exception {
                String url = URLBuilder.buildUrlFromBase(VpnConstant.CHECK_CREATE_STATE_PATH, cmd.getVpnUuid());
                VpnCommands.CheckStatusResponse rsp =
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
        APIUpdateVpnExpireDateEvent evt = new APIUpdateVpnExpireDateEvent(msg.getId());

        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        LocalDateTime newTime = vpn.getExpireDate().toLocalDateTime();
        boolean success = false;
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
                success = createOrder(renewOrderMsg);
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
                success = createOrder(slaCompensationOrderMsg);
                newTime = newTime.plusDays(msg.getDuration());
                break;
        }
        if (success) {
            vpn.setExpireDate(Timestamp.valueOf(newTime));
            vpn = dbf.getEntityManager().merge(vpn);
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
        }

        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnBandwidthMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());

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

        if (!createOrder(orderMsg)) {
            evt.setError(errf.stringToOperationError("订单操作失败"));
            bus.publish(evt);
            return;
        }


        vpn = dbf.getEntityManager().merge(vpn);
        dbf.getEntityManager().persist(record);

        VpnCommands.UpdateVpnBandWidthCmd cmd = VpnCommands.UpdateVpnBandWidthCmd.valueOf(vpn);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.UPDATE_VPN_BANDWIDTH_PATH, cmd);
        if (!result.isSuccess()) {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }

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
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        vpn.setState(msg.getState());

        VpnAgentCommand cmd = null;
        String path = null;
        switch (msg.getState()) {
            case Enabled:
                cmd = VpnCommands.CreateVpnCmd.valueOf(vpn);
                path = VpnConstant.START_VPN_PATH;
                break;
            case Disabled:
                cmd = VpnCommands.StopVpnCmd.valueOf(vpn);
                path = VpnConstant.STOP_VPN_PATH;
                break;
        }
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller().syncPostForResult(path, cmd);

        if (result.isSuccess()) {
            vpn = dbf.getEntityManager().merge(vpn);
            evt.setInventory(VpnInventory.valueOf(vpn));
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }

        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnCidrMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        vpn.setVpnCidr(msg.getVpnCidr());

        VpnCommands.UpdateVpnCidrCmd cmd = VpnCommands.UpdateVpnCidrCmd.valueOf(vpn);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.UPDATE_VPN_CIDR_PATH, cmd);
        if (result.isSuccess()) {
            vpn = dbf.getEntityManager().merge(vpn);
            evt.setInventory(VpnInventory.valueOf(vpn));
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }

        bus.publish(evt);
    }


    @Transactional
    public void handle(APIDeleteVpnMsg msg) {
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductName(vpn.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        if (!createOrder(orderMsg)) {
            evt.setError(errf.stringToOperationError("退订失败"));
            bus.publish(evt);
            return;
        }

        VpnCommands.DeleteVpnCmd cmd = VpnCommands.DeleteVpnCmd.valueOf(vpn);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.DELETE_VPN_PATH, cmd);
        if (result.isSuccess()) {
            dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }
        bus.publish(evt);
    }

    @Transactional
    public void handle(APICreateVpnInterfaceMsg msg) {
        APICreateVpnInterfaceEvent evt = new APICreateVpnInterfaceEvent(msg.getId());

        VpnInterfaceVO iface = new VpnInterfaceVO();
        iface.setUuid(Platform.getUuid());
        iface.setVpnUuid(msg.getVpnUuid());
        iface.setName(msg.getName());
        iface.setNetworkUuid(msg.getTunnelUuid());
        iface.setLocalIp(msg.getLocalIP());
        iface.setNetmask(msg.getNetmask());

        VpnCommands.VpnInterfaceCmd cmd = VpnCommands.VpnInterfaceCmd.valueOf(msg.getLocalIP(), iface);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.ADD_VPN_INTERFACE_PATH, cmd);
        if (result.isSuccess()) {
            iface = dbf.persistAndRefresh(iface);
            evt.setInventory(VpnInterfaceInventory.valueOf(iface));
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnInterfaceMsg msg) {
        APIDeleteVpnInterfaceEvent evt = new APIDeleteVpnInterfaceEvent(msg.getId());
        VpnInterfaceVO iface = dbf.findByUuid(msg.getUuid(), VpnInterfaceVO.class);

        VpnVO vpn = dbf.findByUuid(iface.getVpnUuid(), VpnVO.class);
        VpnCommands.VpnInterfaceCmd cmd = VpnCommands.VpnInterfaceCmd.valueOf(vpn.getVpnHost().getManageIp(), iface);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.DELETE_VPN_INTERFACE_PATH, cmd);
        if (result.isSuccess()) {
            dbf.removeByPrimaryKey(msg.getUuid(), VpnInterfaceVO.class);
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }
        bus.publish(evt);
    }

    @Transactional
    public void handle(APICreateVpnRouteMsg msg) {
        APICreateVpnRouteEvent evt = new APICreateVpnRouteEvent(msg.getId());

        VpnRouteVO route = new VpnRouteVO();
        route.setVpnUuid(msg.getVpnUuid());
        route.setRouteType(msg.getRouteType());
        route.setNextInterface(msg.getNextIface());
        route.setTargetCidr(msg.getTargetCidr());

        VpnVO vpn = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        VpnCommands.VpnRouteCmd cmd = VpnCommands.VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.ADD_VPN_ROUTE_PATH, cmd);

        if (result.isSuccess()) {
            route = dbf.persistAndRefresh(route);
            evt.setInventory(VpnRouteInventory.valueOf(route));
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }

        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnRouteMsg msg) {
        APIDeleteVpnRouteEvent evt = new APIDeleteVpnRouteEvent(msg.getId());
        VpnRouteVO route = dbf.findByUuid(msg.getUuid(), VpnRouteVO.class);

        VpnVO vpn = dbf.findByUuid(route.getVpnUuid(), VpnVO.class);
        VpnCommands.VpnRouteCmd cmd = VpnCommands.VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);
        VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                .syncPostForResult(VpnConstant.DELETE_VPN_ROUTE_PATH, cmd);
        if (result.isSuccess()) {
            dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
        } else {
            evt.setError(errf.stringToOperationError(result.getMessage()));
        }
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
            VpnCommands.ReconnectVpnCmd cmd = VpnCommands.ReconnectVpnCmd.valueOf(vpn);
            VpnAgentResponse.VpnTaskResult result = new VpnRESTCaller()
                    .syncPostForResult(VpnConstant.RECONNECT_VPN_PATH, cmd);

            return result.isSuccess();
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
                    VpnCommands.CheckVpnStatusCmd cmd = VpnCommands.CheckVpnStatusCmd.valueOf(vo);
                    VpnCommands.CheckStatusResponse rsp =
                            new VpnRESTCaller().checkState(VpnConstant.CHECK_VPN_STATUS_PATH, cmd);
                    if (rsp.getStatusCode() != HttpStatus.OK || rsp.getStatus() != VpnCommands.RunStatus.UP) {
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
