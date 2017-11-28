package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.*;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.host.HostStatus;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.query.QueryOp;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.billingCallBack.*;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.exception.VpnServiceException;
import com.syscxp.header.vpn.host.*;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.vpn.vpn.VpnCommands.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.*;

@Component
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
        } else if (msg instanceof APIGetVpnMsg) {
            handle((APIGetVpnMsg) msg);
        } else if (msg instanceof APIGetVpnPriceMsg) {
            handle((APIGetVpnPriceMsg) msg);
        } else if (msg instanceof APIUpdateVpnStateMsg) {
            handle((APIUpdateVpnStateMsg) msg);
        } else if (msg instanceof APIDownloadCertificateMsg) {
            handle((APIDownloadCertificateMsg) msg);
        } else if (msg instanceof APIResetCertificateMsg) {
            handle((APIResetCertificateMsg) msg);
        }else if (msg instanceof APIRenewVpnMsg) {
            handle((APIRenewVpnMsg) msg);
        } else if (msg instanceof APIRenewAutoVpnMsg) {
            handle((APIRenewAutoVpnMsg) msg);
        } else if (msg instanceof APISLAVpnMsg) {
            handle((APISLAVpnMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIResetCertificateMsg msg) {
        final APIResetCertificateEvent evt = new APIResetCertificateEvent(msg.getId());

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(generateCertKey(msg.getAccountUuid(), vpn.getSid()));

        ResetCertificateCmd cmd = ResetCertificateCmd.valueOf(vpn);

        new VpnRESTCaller().sendCommand(VpnConstant.SERVICE_PATH, cmd, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInventory.valueOf(dbf.updateAndRefresh(vpn)));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
            }
        });

        bus.publish(evt);

    }

    private void handle(APIDownloadCertificateMsg msg) {
        final APIDownloadCertificateEvent evt = new APIDownloadCertificateEvent(msg.getId());
        VpnVO vpn = Q.New(VpnVO.class).eq(VpnVO_.sid, msg.getSid()).find();

        bus.publish(evt);
    }

    private void handle(APIGetVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        VpnInventory inventory = VpnInventory.valueOf(vpn);

        APIGetVpnReply reply = new APIGetVpnReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);
    }

    private void handle(APIGetVpnPriceMsg msg) {
        APIGetVpnPriceReply reply = getVpnPrice(msg.getDuration(), msg.getBandwidthOfferingUuid());

        bus.reply(msg, reply);
    }

    private String generateCertKey(String accountUuid, String sid) {
        return DigestUtils.md5Hex(accountUuid + sid + VpnConstant.GENERATE_KEY);
    }

    private void handle(APICreateVpnMsg msg) {
        APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());

        VpnHostVO host = dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class);

        VpnVO vpn = new VpnVO();
        vpn.setUuid(Platform.getUuid());
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(generateCertKey(msg.getAccountUuid(), vpn.getSid()));
        vpn.setMaxModifies(VpnGlobalProperty.VPN_MAX_MOTIFIES);
        vpn.setAccountUuid(msg.getAccountUuid());
        vpn.setDescription(msg.getDescription());
        vpn.setName(msg.getName());
        vpn.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vpn.setEndpointUuid(msg.getEndpointUuid());
        vpn.setPayment(Payment.UNPAID);
        vpn.setState(VpnState.Enabled);
        vpn.setStatus(VpnStatus.Connecting);
        vpn.setDuration(msg.getDuration());
        vpn.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));
        vpn.setPort(generatePort(host));
        vpn.setHostUuid(msg.getHostUuid());

        dbf.persistAndRefresh(vpn);

        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        ProductInfoForOrder productInfoForOrder = createBuyOrderForVPN(vpn, new CreateVpnCallBack());
        productInfoForOrder.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));

        APICreateBuyOrderReply reply = createOrder(orderMsg);

        if (!reply.isOrderSuccess()) {
            evt.setError(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "付款失败"));
            evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
            bus.publish(evt);
            return;
        }

        vpn.setPayment(Payment.PAID);
        final VpnVO vo = dbf.updateAndRefresh(vpn);

        CreateVpnCmd cmd = CreateVpnCmd.valueOf(vo);
        String url = buildUrl(host.getHostIp(), VpnGlobalProperty.AGENT_PORT, VpnConstant.SERVICE_PATH);

        new VpnRESTCaller().sendCommand(url, cmd, new Completion(evt) {
            @Override
            public void success() {
                vo.setStatus(VpnStatus.Connected);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                vo.setStatus(VpnStatus.Disconnected);
                evt.setError(errorCode);
            }
        });

        evt.setInventory(VpnInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(evt);
    }

    private ProductInfoForOrder createBuyOrderForVPN(VpnVO vo, NotifyCallBackData callBack) {
        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductChargeModel(ProductChargeModel.BY_MONTH);
        order.setDuration(vo.getDuration());
        order.setProductName(vo.getName());
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.VPN);
        order.setDescriptionData(getDescriptionForVPN(vo));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setAccountUuid(vo.getAccountUuid());
        order.setUnits(generateUnits(vo.getBandwidthOfferingUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    private APICreateOrderMsg getOrderMsgForVPN(VpnVO vo, NotifyCallBackData callBack) {
        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setDescriptionData(getDescriptionForVPN(vo));
        orderMsg.setAccountUuid(vo.getAccountUuid());
        orderMsg.setUnits(generateUnits(vo.getBandwidthOfferingUuid()));
        orderMsg.setCallBackData(RESTApiDecoder.dump(callBack));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        return orderMsg;
    }

    private String getDescriptionForVPN(VpnVO vo) {
        DescriptionData data = new DescriptionData();
        data.add(new DescriptionItem("name", vo.getName()));
        data.add(new DescriptionItem("bandwidth", vo.getBandwidthOfferingUuid()));
        return JSONObjectUtil.toJsonString(data);
    }

    private List<ProductPriceUnit> generateUnits(String bandwidth) {

        return CollectionDSL.list(ProductPriceUnitFactory
                .createVpnPriceUnit(bandwidth));
    }

    private <T extends APIReply> T createOrder(APIMessage orderMsg) {
        String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.BILLING_SERVER_URL, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(orderMsg);
        APIReply reply;
        try {
            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(orderMsg), RestAPIResponse.class);
            reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (!reply.isSuccess())
                throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "failed to operate order."));
        } catch (Exception e) {
            throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, String.format("call billing[url: %s] failed.", url)));
        }
        return reply.castReply();
    }

    @Deferred
    private Integer generatePort(VpnHostVO host) {
        GLock lock = new GLock("VpnPort", TimeUnit.MINUTES.toSeconds(2));
        lock.lock();
        Defer.defer(lock::unlock);

        Integer port = Q.New(VpnVO.class).eq(VpnVO_.hostUuid, host.getUuid())
                .orderBy(VpnVO_.port, SimpleQuery.Od.DESC)
                .select(VpnVO_.port)
                .limit(1).findValue();
        if (port == null)
            return host.getStartPort();
        if (port >= host.getEndPort()) {
            throw new VpnServiceException(
                    argerr("All port in the host[uuid:%s] already used.", host.getUuid()));

        }
        return port + 1;
    }

    private void handle(APIRenewVpnMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(getOrderMsgForVPN(vo, new RenewVpnCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);

        afterRenewVpn(reply, vo, msg);
    }

    private void afterRenewVpn(APICreateOrderReply orderReply, VpnVO vo, APIMessage msg) {
        APIRenewVpnReply reply = new APIRenewVpnReply();

        if (!orderReply.isOrderSuccess()) {
            reply.setError(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "订单操作失败", orderReply.getError()));
            bus.reply(msg, reply);
            return;
        }

        OrderInventory inventory = orderReply.getInventory();
        vo.setDuration(inventory.getDuration());

        vo.setExpireDate(getExpireDate(vo.getExpireDate(), inventory.getProductChargeModel(), inventory.getDuration()));

        vo = dbf.updateAndRefresh(vo);
        reply.setInventory(VpnInventory.valueOf(vo));
        bus.reply(msg, reply);
    }

    private Timestamp getExpireDate(Timestamp oldTime, ProductChargeModel chargeModel, int duration) {
        Timestamp newTime = oldTime;
        if (chargeModel == ProductChargeModel.BY_MONTH) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusMonths(duration));
        } else if (chargeModel == ProductChargeModel.BY_YEAR) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusYears(duration));
        } else if (chargeModel == ProductChargeModel.BY_DAY) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusDays(duration));
        }
        return newTime;
    }


    private void handle(APIRenewAutoVpnMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(getOrderMsgForVPN(vo, new RenewAutoVpnCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setOpAccountUuid("system");
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);

        afterRenewVpn(reply, vo, msg);
    }

    private void handle(APISLAVpnMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        APICreateSLACompensationOrderMsg orderMsg = new APICreateSLACompensationOrderMsg(getOrderMsgForVPN(vo, new SlaVpnCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);
        afterRenewVpn(reply, vo, msg);
    }

    private void saveMotifyRecord(APIUpdateVpnBandwidthMsg msg) {
        VpnMotifyRecordVO record = new VpnMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setVpnUuid(msg.getUuid());
        record.setOpAccountUuid(msg.getOpAccountUuid());
        dbf.persistAndRefresh(record);
    }

    public void handle(APIUpdateVpnBandwidthMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());

        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductName(vpn.getName());
        orderMsg.setDescriptionData(vpn.getDescription());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid()));
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);

        if (!reply.isOrderSuccess()) {
            reply.setError(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "订单操作失败", reply.getError()));
            bus.publish(evt);
            return;
        }

        vpn.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vpn.setStatus(VpnStatus.Disconnected);
        final VpnVO vo = dbf.updateAndRefresh(vpn);
        saveMotifyRecord(msg);

        String url = buildUrl(vpn.getVpnHost().getHostIp(), VpnGlobalProperty.AGENT_PORT, VpnConstant.SERVICE_PATH);
        UpdateVpnBandWidthCmd cmd = UpdateVpnBandWidthCmd.valueOf(vo);

        new VpnRESTCaller().sendCommand(url, cmd, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInventory.valueOf(vo));
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });
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

    public void handle(APIUpdateVpnStateMsg msg) {
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        VpnState next = msg.getState();
        if (vpn.getState() == next) {
            evt.setInventory(VpnInventory.valueOf(vpn));
            bus.publish(evt);
            return;
        }
        vpn.setState(next);
        vpn.setStatus(VpnStatus.Connected);
        VpnAgentCommand cmd = CreateVpnCmd.valueOf(vpn);

        String path = VpnConstant.SERVICE_PATH;
        if (next == VpnState.Disabled) {
            vpn.setStatus(VpnStatus.Disconnected);
            cmd = DeleteVpnCmd.valueOf(vpn);
            path = VpnConstant.SERVICE_PATH;
        }

        String url = buildUrl(vpn.getVpnHost().getHostIp(), VpnGlobalProperty.AGENT_PORT, path);
        new VpnRESTCaller().sendCommand(url, cmd, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInventory.valueOf(dbf.updateAndRefresh(vpn)));
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });
    }

    public void handle(APIDeleteVpnMsg msg) {
        APIDeleteVpnEvent evt = new APIDeleteVpnEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductName(vpn.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);

        if (!reply.isOrderSuccess()) {
            reply.setError(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "退订失败", reply.getError()));
            bus.publish(evt);
            return;
        }

        vpn.setState(VpnState.Disabled);
        final VpnVO vo = dbf.updateAndRefresh(vpn);

        DeleteVpnCmd cmd = DeleteVpnCmd.valueOf(vpn);
        String url = buildUrl(vpn.getVpnHost().getHostIp(), VpnGlobalProperty.AGENT_PORT, VpnConstant.SERVICE_PATH);
        new VpnRESTCaller().sendCommand(url, cmd, new Completion(evt) {
            @Override
            public void success() {
                dbf.remove(vo);
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });

    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }


    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.SERVICE_ID);
    }

    private Future<Void> vpnCheckThread;
    private int vpnStatusCheckWorkerInterval;
    private List<String> disconnectedVpn = new ArrayList<>();

    private void startFailureHostCopingThread() {
        vpnCheckThread = thdf.submitPeriodicTask(new VpnStatusCheckWorker(), 60);
        logger.debug(String
                .format("security group failureHostCopingThread starts[failureHostWorkerInterval: %ss]",
                        vpnStatusCheckWorkerInterval));
    }

    private void restartFailureHostCopingThread() {
        if (vpnCheckThread != null) {
            vpnCheckThread.cancel(true);
        }
        startFailureHostCopingThread();
    }

    private void prepareGlobalConfig() {
        vpnStatusCheckWorkerInterval = VpnGlobalProperty.VPN_STATUS_CHECK_WORKER_INTERVAL;
        restartFailureHostCopingThread();
    }

    private class VpnStatusCheckWorker implements PeriodicTask {
        @Transactional
        private List<VpnVO> getAllVpns() {
            return Q.New(VpnVO.class)
                    .eq(VpnVO_.state, VpnState.Enabled)
                    .notEq(VpnVO_.status, VpnStatus.Connecting)
                    .list();
        }

        private void updateVpnStatus(List<String> vpnUuids, VpnStatus status) {
            if (vpnUuids.isEmpty())
                return;
            logger.debug(String.format("update vpn status %s, uuid in %s", status, JSONObjectUtil.toJsonString(vpnUuids)));
            UpdateQuery.New(VpnVO.class).in(VpnVO_.uuid, vpnUuids)
                    .set(VpnVO_.status, status).update();
        }

        @Override
        public void run() {

            disconnectedVpn.clear();
            List<VpnVO> vos = getAllVpns();
            logger.debug("start check vpn status.");
            if (vos.isEmpty()) {
                return;
            }
            for (VpnVO vo : vos) {
                CheckVpnStatusCmd cmd = CheckVpnStatusCmd.valueOf(vo);
                RunStatus status = new VpnRESTCaller().checkStatus(VpnConstant.SERVICE_PATH, cmd);

                if (status == RunStatus.UP) {
                    if (vo.getStatus() == VpnStatus.Disconnected)
                        updateVpnStatus(Collections.singletonList(vo.getUuid()), VpnStatus.Connected);
                    continue;
                }

                if (!reconnectVpn(vo)) {
                    disconnectedVpn.add(vo.getUuid());
                }
            }
            updateVpnStatus(disconnectedVpn, VpnStatus.Disconnected);
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
        restf.registerSyncHttpCallHandler("billing", OrderCallbackCmd.class,
                cmd -> {
                    Message message = RESTApiDecoder.loads(cmd.getCallBackData());
                    if (message instanceof CreateVpnCallBack) {
                        VpnVO vpn = updateVpnFromOrder(cmd);
                        if (vpn != null && vpn.getStatus() == VpnStatus.Connecting) {
                            String url = buildUrl(vpn.getVpnHost().getHostIp(), VpnGlobalProperty.AGENT_PORT, VpnConstant.SERVICE_PATH);
                            new VpnRESTCaller().sendCommand(url, CreateVpnCmd.valueOf(vpn),
                                    new Completion(null) {
                                        @Override
                                        public void success() {
                                            vpn.setStatus(VpnStatus.Connected);
                                        }

                                        @Override
                                        public void fail(ErrorCode errorCode) {
                                            vpn.setStatus(VpnStatus.Disconnected);

                                        }
                                    });
                        }
                        return null;
                    } else if (message instanceof DeleteVpnCallBack) {

                    } else if (message instanceof RenewAutoVpnCallBack) {

                    } else if (message instanceof RenewVpnCallBack) {
                        updateVpnFromOrder(cmd);
                        return null;
                    } else if (message instanceof SlaVpnCallBack) {
                        updateVpnFromOrder(cmd);
                        return null;
                    } else if (message instanceof UnsubcribeVpnCallBack) {
                        VpnVO vpn = dbf.findByUuid(cmd.getPorductUuid(), VpnVO.class);
                        if (vpn != null) {
                            deleteVpn(vpn);
                        }
                        return null;
                    } else if (message instanceof UpdateVpnBandwidthCallBack) {
                        VpnVO vpn = updateVpnFromOrder(cmd);
                        if (vpn != null && vpn.getStatus() == VpnStatus.Disconnected)
                            reconnectVpn(vpn);
                        updateMotifyRecord(cmd);
                        return null;
                    } else {
                        logger.debug("未知回调！！！！！！！！");
                    }
                    return null;
                });
        return true;
    }

    private void updateMotifyRecord(OrderCallbackCmd cmd) {
        VpnMotifyRecordVO record = dbf.getEntityManager().find(VpnMotifyRecordVO.class, cmd.getPorductUuid());
        record.setMotifyType(MotifyType.valueOf(cmd.getType().toString()));
        dbf.update(record);
    }

    private VpnVO updateVpnFromOrder(OrderCallbackCmd cmd) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, cmd.getPorductUuid());
        boolean update = false;
        if (vpn.getPayment() == Payment.UNPAID) {
            vpn.setPayment(Payment.PAID);
            update = true;
        }
        if (cmd.getExpireDate() != null) {
            vpn.setExpireDate(cmd.getExpireDate());
            update = true;
        }
        return update ? dbf.updateAndRefresh(vpn) : null;
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
        } else if (msg instanceof APIDownloadCertificateMsg) {
            validate((APIDownloadCertificateMsg) msg);
        }
        return msg;
    }


    private void validate(APIDownloadCertificateMsg msg) {

        VpnVO vpn = Q.New(VpnVO.class).eq(VpnVO_.sid, msg.getSid()).find();

        byte[] bytes = (vpn.getAccountUuid() + vpn.getSid() + vpn.getCertKey() + msg.getTimestamp()).getBytes();

        Arrays.sort(bytes);

        if (!DigestUtils.md5Hex(bytes).equals(msg.getSignature()))
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[sid:%s]验证不通过.", msg.getSid()));
    }

    private void validate(APIUpdateVpnBandwidthMsg msg) {
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(VpnMotifyRecordVO.class).eq(VpnMotifyRecordVO_.vpnUuid, msg.getUuid())
                .gte(VpnMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime)).count();
        Integer maxModifies =
                Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).select(VpnVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new OperationFailureException(
                    operr("The Vpn[uuid:%s] has motified %s times.", msg.getUuid(), times));
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
        // 区分管理员账户
        if (msg.getSession().isAdminSession() && StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] is not a admin or proxy.",
                            msg.getSession().getAccountUuid()));
        }
        // 物理机
        String hostUuid = Q.New(HostInterfaceVO.class)
                .eq(HostInterfaceVO_.endpointUuid, msg.getEndpointUuid())
                .select(HostInterfaceVO_.hostUuid).findValue();

        if (hostUuid == null)
            throw new ApiMessageInterceptionException(
                    argerr("The Host of the endpoint[uuid:%s] does not exist.", msg.getEndpointUuid()));
        msg.setHostUuid(hostUuid);
        APIGetVpnPriceReply reply = getVpnPrice(msg.getDuration(), msg.getBandwidthOfferingUuid());
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }

    private APIGetVpnPriceReply getVpnPrice(Integer duration, String bandwidthOfferingUuid) {
        APIGetProductPriceMsg msg = new APIGetProductPriceMsg();
        msg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        msg.setDuration(duration);
        msg.setUnits(generateUnits(bandwidthOfferingUuid));
        return createOrder(msg);
    }


    private boolean reconnectVpn(VpnVO vo) {
        if (vo.getVpnHost().getStatus() == HostStatus.Disconnected ||
                vo.getState() == VpnState.Disabled) {
            return false;
        }
        CreateVpnCmd cmd = CreateVpnCmd.valueOf(vo);
        try {
            new VpnRESTCaller().sendCommand(VpnConstant.SERVICE_PATH, cmd, new Completion(null) {
                @Override
                public void success() {
                    if (vo.getStatus() != VpnStatus.Connected)
                        UpdateQuery.New(VpnVO.class).eq(VpnVO_.uuid, vo.getUuid())
                                .set(VpnVO_.status, VpnStatus.Connected).update();
                }

                @Override
                public void fail(ErrorCode errorCode) {
                    if (vo.getStatus() != VpnStatus.Disconnected)
                        UpdateQuery.New(VpnVO.class).eq(VpnVO_.uuid, vo.getUuid())
                                .set(VpnVO_.status, VpnStatus.Disconnected).update();
                    throw new VpnServiceException(errorCode);
                }
            });
        } catch (VpnServiceException e) {
            logger.info(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean deleteVpn(VpnVO vo) {
        TaskResult result;
        try {
            result = new VpnRESTCaller()
                    .syncPostForResult(VpnConstant.SERVICE_PATH, DeleteVpnCmd.valueOf(vo));
            if (result.isSuccess()) {
                dbf.removeByPrimaryKey(vo.getUuid(), VpnVO.class);
            } else {
                UpdateQuery.New(VpnVO.class).eq(VpnVO_.uuid, vo.getUuid())
                        .set(VpnVO_.state, VpnState.Disabled).update();
            }
        } catch (Exception e) {
            return false;
        }

        return result.isSuccess();
    }

    private String buildUrl(String hostIp, Integer port, String path) {
        return URLBuilder.buildHttpUrl(hostIp, port, path);
    }
}
