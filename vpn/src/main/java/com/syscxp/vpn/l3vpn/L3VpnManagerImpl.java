package com.syscxp.vpn.l3vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.*;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.billing.*;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.host.HostState;
import com.syscxp.header.host.HostStatus;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.billingCallBack.CreateVpnCallBack;
import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.header.vpn.l3vpn.APICreateL3VpnMsg;
import com.syscxp.header.vpn.l3vpn.APICreateL3VpnEvent;
import com.syscxp.header.vpn.l3vpn.L3VpnInventory;
import com.syscxp.header.vpn.l3vpn.L3VpnVO;
import com.syscxp.header.vpn.l3vpn.L3VpnVO_;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.vpn.client.VpnBase;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.exception.VpnServiceException;
import com.syscxp.vpn.job.DeleteRenewVOAfterDeleteResourceJob;
import com.syscxp.vpn.client.VpnCommands;
import com.syscxp.vpn.vpn.VpnGlobalProperty;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;

public class L3VpnManagerImpl extends AbstractService implements ApiMessageInterceptor {
    private static final CLogger LOGGER = Utils.getLogger(L3VpnManagerImpl.class);

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade restf;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private JobQueueFacade jobf;

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
        if (msg instanceof CheckVpnStatusMsg) {
            handle((CheckVpnStatusMsg) msg);
        } else if (msg instanceof DeleteVpnMsg) {
            handle((DeleteVpnMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(final CheckVpnStatusMsg msg) {
        CheckVpnStatusReply reply = new CheckVpnStatusReply();

        VpnHostVO host = dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class);
        if (!msg.isNoStatusCheck() && host.getStatus() != HostStatus.Connected) {
            reply.setError(operr("物理机[uuid:%s, status:%s]状态异常", host.getUuid(), host.getStatus()));
            bus.reply(msg, reply);
            return;
        }

        VpnCommands.VpnStatusCmd cmd = new VpnCommands.VpnStatusCmd();
        cmd.vpnuuids = msg.getVpnUuids();

        String baseUrl = URLBuilder.buildHttpUrl(host.getHostIp(), VpnGlobalProperty.AGENT_PORT, VpnConstant.CHECK_VPN_STATUS_PATH);

        sendCommand(baseUrl, cmd, VpnCommands.VpnStatusRsp.class, new ReturnValueCompletion<VpnCommands.VpnStatusRsp>(reply) {

            @Override
            public void success(VpnCommands.VpnStatusRsp ret) {
                if (!ret.isSuccess()) {
                    reply.setError(operr(ret.getError()));
                } else {
                    Map<String, String> m = new HashMap<>(ret.states.size());
                    for (Map.Entry<String, String> e : ret.states.entrySet()) {
                        if ("UP".equals(e.getValue())) {
                            m.put(e.getKey(), VpnStatus.Connected.toString());
                        } else {
                            m.put(e.getKey(), VpnStatus.Disconnected.toString());
                        }
                    }
                    reply.setStates(m);
                }
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(DeleteVpnMsg msg) {
        DeleteVpnReply reply = new DeleteVpnReply();
        VpnVO vpn = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        if (vpn == null) {
            bus.reply(msg, reply);
            return;
        }
        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("delete-vpn-%s", msg.getVpnUuid()));
        chain.then(new NoRollbackFlow() {
            String __name__ = "detach-vpn-cert";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                if (vpn.getVpnCertUuid() != null) {
                    detachVpnCert(vpn.getUuid(), vpn.getVpnCert().getUuid());
                    LOGGER.debug(String.format("VPN[UUID:%s]解绑证书[UUID:%s]成功", vpn.getUuid(), vpn.getVpnCertUuid()));
                }
                trigger.next();
            }
        }).then(new NoRollbackFlow() {
            String __name__ = "destroy-vpn";

            @Override
            public void run(final FlowTrigger trigger, Map data) {

                DestroyVpnMsg destroyVpnMsg = new DestroyVpnMsg();
                destroyVpnMsg.setVpnUuid(vpn.getUuid());
                bus.makeLocalServiceId(destroyVpnMsg, VpnConstant.L3_SERVICE_ID);
                bus.send(destroyVpnMsg, new CloudBusCallBack(trigger) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            LOGGER.debug(String.format("VPN[UUID:%s]销毁成功", vpn.getUuid()));
                            trigger.next();
                        } else {
                            LOGGER.debug(String.format("VPN[UUID:%s]销毁失败", vpn.getUuid()));
                            trigger.fail(reply.getError());
                        }
                    }
                });
            }
        });
        chain.done(new FlowDoneHandler(msg) {
            @Override
            public void handle(Map data) {
                if (msg.isDeleteRenew()) {
                    DeleteRenewVOAfterDeleteResourceJob.execute(jobf, vpn.getUuid(), vpn.getAccountUuid());
                }
                dbf.removeByPrimaryKey(vpn.getUuid(), VpnVO.class);
                bus.reply(msg, reply);
            }
        }).error(new FlowErrorHandler(msg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                reply.setError(errCode);
                bus.reply(msg, reply);
            }
        }).start();
    }

    @Transactional
    private void detachVpnCert(String vpnUuid, String vpnCertUuid) {
        VpnVO vpnVO = dbf.getEntityManager().find(VpnVO.class, vpnUuid);
        vpnVO.setVpnCertUuid(null);
        dbf.getEntityManager().merge(vpnVO);

        VpnCertVO vpnCert = dbf.getEntityManager().find(VpnCertVO.class, vpnCertUuid);
        vpnCert.setVpnNum(vpnCert.getVpnNum() - 1);
        dbf.getEntityManager().merge(vpnCert);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateL3VpnMsg) {
            handle((APICreateL3VpnMsg) msg);
        }
//        else if (msg instanceof APIUpdateVpnMsg){
//            handle((APIUpdateVpnMsg) msg);
//        }
        else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateL3VpnMsg msg) {
        final APICreateL3VpnEvent evt = new APICreateL3VpnEvent(msg.getId());
        doAddVpn(msg, new ReturnValueCompletion<L3VpnInventory>(msg) {
            @Override
            public void success(L3VpnInventory inventory) {
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


    private void doAddVpn(final APICreateL3VpnMsg msg, ReturnValueCompletion<L3VpnInventory>  completion) {
        VpnHostVO host = dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class);
        checkHostState(host);

        L3VpnVO vo = new L3VpnVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setDescription(msg.getDescription());
        vo.setHostUuid(msg.getHostUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setL3EndpointUuid(msg.getL3EndpointUuid());
        vo.setL3NetworkUuid(msg.getL3NetworkUuid());
        vo.setWorkMode(msg.getWorkMode());
        vo.setPort(generatePort(host));
        vo.setVlan(msg.getVlan());
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setDuration(msg.getDuration());
        vo.setPayment(Payment.UNPAID);
        vo.setState(VpnState.Disabled);
        vo.setStatus(VpnStatus.Disconnected);
        vo.setMaxModifies(VpnGlobalProperty.VPN_MAX_MOTIFIES);
        vo.setSecretId(Platform.getUuid());
        vo.setSecretKey(generateSecretKey(msg.getAccountUuid(), vo.getSecretId()));
        dbf.persistAndRefresh(vo);
        LOGGER.debug(String.format("数据库保存VPN[name:%s, uuid:%s]成功", vo.getName(), vo.getUuid()));

        attachVpnCert(vo.getUuid(), msg.getVpnCertUuid());

        final L3VpnVO vpn = dbf.findByUuid(vo.getUuid(), L3VpnVO.class);

        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("add-vpn-%s", vpn.getUuid()));

        chain.then(new NoRollbackFlow() {
            String __name__ = "pay-before-add-vpn";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
                ProductInfoForOrder productInfoForOrder = createBuyOrderForVPN(vpn, msg.getProductChargeModel(), new CreateVpnCallBack());
                productInfoForOrder.setOpAccountUuid(msg.getSession().getAccountUuid());
                orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));
                createOrder(orderMsg, new Completion(trigger) {
                    @Override
                    public void success() {
                        vpn.setState(VpnState.Enabled);
                        vpn.setStatus(VpnStatus.Connecting);
                        vpn.setPayment(Payment.PAID);
                        vpn.setExpireDate(generateExpireDate(dbf.getCurrentSqlTime(), msg.getDuration(), msg.getProductChargeModel()));
                        dbf.updateAndRefresh(vpn);
                        LOGGER.debug(String.format("VPN[name:%s, uuid:%s]付款成功", vo.getName(), vo.getUuid()));
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        LOGGER.debug(String.format("VPN[name:%s, uuid:%s]付款失败", vo.getName(), vo.getUuid()));
                        trigger.fail(errorCode);
                    }
                });
            }

        }).then(new NoRollbackFlow() {
            String __name__ = "send-init-vpn-message";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                LOGGER.debug(String.format("VPN[name:%s, uuid:%s]初始化", vo.getName(), vo.getUuid()));
                InitVpnMsg initVpnMsg = new InitVpnMsg();
                initVpnMsg.setVpnUuid(vpn.getUuid());
                bus.makeLocalServiceId(initVpnMsg, VpnConstant.SERVICE_ID);
                bus.send(initVpnMsg, new CloudBusCallBack(trigger) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            trigger.next();
                        } else {
                            trigger.fail(reply.getError());
                        }
                    }
                });
                trigger.next();
            }
        }).done(new FlowDoneHandler(msg) {
            @Override
            public void handle(Map data) {
                L3VpnInventory inv = L3VpnInventory.valueOf(dbf.reload(vo));
                LOGGER.debug(String.format("创建VPN[name:%s, uuid:%s]成功", vo.getName(), vo.getUuid()));
                completion.success(inv);
            }
        }).error(new FlowErrorHandler(msg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                LOGGER.debug(String.format("创建vpn[name:%s, uuid:%s]失败", vo.getName(), vo.getUuid()));
                completion.fail(errCode);
            }
        }).start();
    }

    private Timestamp generateExpireDate(Timestamp start, Integer duration, ProductChargeModel model) {
        switch (model) {
            case BY_DAY:
                return Timestamp.valueOf(start.toLocalDateTime().plusDays(duration));
            case BY_WEEK:
                return Timestamp.valueOf(start.toLocalDateTime().plusWeeks(duration));
            case BY_MONTH:
                return Timestamp.valueOf(start.toLocalDateTime().plusMonths(duration));
            case BY_YEAR:
                return Timestamp.valueOf(start.toLocalDateTime().plusYears(duration));
            default:
                return Timestamp.valueOf(start.toLocalDateTime().plusMonths(duration));
        }
    }
    private ProductInfoForOrder createBuyOrderForVPN(L3VpnVO vo, ProductChargeModel model, NotifyCallBackData callBack) {
        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductChargeModel(model);
        order.setDuration(vo.getDuration());
        order.setProductName(vo.getName());
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.VPN);
        order.setDescriptionData(getDescriptionForVPN(vo.getBandwidthOfferingUuid()));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setAccountUuid(vo.getAccountUuid());
        order.setUnits(generateUnits(vo.getBandwidthOfferingUuid(), vo.getL3EndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }
    private String getDescriptionForVPN(String bandwidthOfferingUuid) {
        DescriptionData data = new DescriptionData();
        data.add(new DescriptionItem("带宽", bandwidthOfferingUuid));
        return JSONObjectUtil.toJsonString(data);
    }

    private void createOrder(APIMessage orderMsg, final Completion complete) {

        String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.BILLING_SERVER_URL, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(orderMsg);
        try {
            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(orderMsg), RestAPIResponse.class);
            APIReply apiReply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (apiReply.isSuccess()) {
                complete.success();
            } else {
                LOGGER.debug(String.format("Message[%s]:billing处理失败", orderMsg.getClass().getSimpleName()));
                complete.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "billing处理失败"));
            }
        } catch (Exception e) {
            LOGGER.debug(String.format("call billing[url: %s] failed.", url));
            complete.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, String.format("调用billing[url: %s]失败.", url)));
        }
    }
    @Deferred
    private Integer generatePort(VpnHostVO host) {
        GLock lock = new GLock("VpnPort", TimeUnit.MINUTES.toSeconds(2));
        lock.lock();
        Defer.defer(lock::unlock);

        Integer port = Q.New(L3VpnVO.class).eq(L3VpnVO_.hostUuid, host.getUuid())
                .orderBy(L3VpnVO_.port, SimpleQuery.Od.DESC)
                .select(L3VpnVO_.port)
                .limit(1).findValue();
        if (port == null) {
            return host.getStartPort()+10000;
        }
        if (port >= host.getEndPort()+10000) {
            throw new VpnServiceException(
                    argerr("物理机[uuid:%s]没有可用的端口.", host.getUuid()));

        }
        return port + 1;
    }
    private String generateSecretKey(String accountUuid, String secretId) {
        return DigestUtils.md5Hex(accountUuid + secretId + VpnConstant.URL_GENERATE_KEY);
    }

    @Transactional
    private void attachVpnCert(String vpnUuid, String vpnCertUuid) {
        L3VpnVO vpnVO = dbf.getEntityManager().find(L3VpnVO.class, vpnUuid);
        vpnVO.setVpnCertUuid(vpnCertUuid);
        dbf.getEntityManager().merge(vpnVO);

        VpnCertVO vpnCert = dbf.getEntityManager().find(VpnCertVO.class, vpnCertUuid);
        vpnCert.setVpnNum(vpnCert.getVpnNum() + 1);
        dbf.getEntityManager().merge(vpnCert);
        LOGGER.debug(String.format("VPN[uuid:%s]绑定证书[uuid:%s]成功", vpnUuid, vpnCertUuid));
    }
    @Override
    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.L3_SERVICE_ID);
    }

    private <T extends VpnCommands.AgentResponse> void sendCommand(String url, final VpnCommands.AgentCommand cmd, final Class<T> retClass,
                                                                   final ReturnValueCompletion<T> completion) {
        try {
            T rsp = restf.syncJsonPost(url, cmd, retClass);
            if (rsp.isSuccess()) {
                completion.success(rsp);
            } else {
                LOGGER.debug(String.format("ERROR: %s", rsp.getError()));
                completion.fail(errf.instantiateErrorCode(VpnErrors.CREATE_CERT_ERRORS, rsp.getError()));
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            completion.fail(errf.instantiateErrorCode(SysErrors.HTTP_ERROR, e.getMessage()));
        }
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateL3VpnMsg) {
            validate((APICreateL3VpnMsg) msg);
        }
        return msg;

    }

    private void validate(APICreateL3VpnMsg msg) {
        // 区分管理员账户
        if (msg.getSession().isAdminSession() && StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("管理员账户[uuid:%s]不能给自己创建VPN",
                            msg.getSession().getAccountUuid()));
        }
        Q q = Q.New(L3VpnVO.class).eq(L3VpnVO_.name, msg.getName()).eq(L3VpnVO_.accountUuid, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("VPN[name:%s]已经存在。", msg.getName()));
        }
        // 物理机
        List<String> hostUuids = getHostUuid(msg.getL3EndpointUuid(), msg.getVlan());
        if (hostUuids.isEmpty()) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    String.format("连接点[uuid:%s]下没有可用的物理机.", msg.getL3EndpointUuid())));
        }
        Long sum = Q.New(L3VpnVO.class).in(L3VpnVO_.hostUuid, hostUuids).eq(L3VpnVO_.state, VpnState.Enabled).count();
        msg.setHostUuid(hostUuids.get((int) (sum % hostUuids.size())));
        if (msg.getProductChargeModel() == null)
            msg.setProductChargeModel(ProductChargeModel.BY_MONTH);

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid(), msg.getL3EndpointUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        if (!reply.isPayable()) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.CALL_BILLING_ERROR,
                    String.format("账户[uuid:%s]余额不足.", msg.getAccountUuid())));
        }

    }

    private List<String> getHostUuid(String endpointUuid, Integer vlan) {
        String sql = "select vh.uuid from VpnHostVO vh, HostInterfaceVO hi where vh.uuid = hi.hostUuid " +
                "and hi.endpointUuid = :endpointUuid and vh.status = :status and " +
                ":vlan not in (select v.vlan from L3VpnVO v where vh.uuid = v.hostUuid) ";

        return SQL.New(sql).param("endpointUuid", endpointUuid)
                .param("status", HostStatus.Connected).param("vlan", vlan).list();
    }

    private List<ProductPriceUnit> generateUnits(String bandwidth, String areaCode) {

        return CollectionDSL.list(ProductPriceUnitFactory.createVpnPriceUnit(bandwidth, areaCode));
    }

    private <T extends APIReply> T createOrder(APIMessage orderMsg) {
        String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.BILLING_SERVER_URL, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(orderMsg);
        APIReply reply;
        try {
            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(orderMsg), RestAPIResponse.class);
            reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (!reply.isSuccess()) {
                throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "订单操作失败."));
            }
        } catch (Exception e) {
            throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, String.format("调用billing服务[url: %s]失败.", url)));
        }
        return reply.castReply();
    }

    private void checkHostState(VpnHostVO vo) {
        if (HostState.Disabled == vo.getState()) {
            throw new OperationFailureException(operr("物理机已禁用"));
        }
    }

}
