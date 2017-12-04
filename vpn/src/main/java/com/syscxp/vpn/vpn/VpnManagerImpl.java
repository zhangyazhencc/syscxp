package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.CloudBusSteppingCallback;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.host.HostGlobalProperty;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.host.HostState;
import com.syscxp.header.message.*;
import com.syscxp.header.query.QueryOp;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.billingCallBack.*;
import com.syscxp.header.vpn.host.HostInterfaceVO;
import com.syscxp.header.vpn.host.HostInterfaceVO_;
import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.SizeUnit;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.exception.VpnServiceException;
import com.syscxp.vpn.vpn.VpnCommands.AgentCommand;
import com.syscxp.vpn.vpn.VpnCommands.CreateCertCmd;
import com.syscxp.vpn.vpn.VpnCommands.CreateCertRsp;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;

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

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof VpnMessage) {
            passThrough((VpnMessage) msg);
        } else if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void passThrough(VpnMessage msg) {
        VpnVO vo = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        if (vo == null) {
            String err = String.format("unable to find vpn[uuid=%s]", msg.getVpnUuid());
            bus.replyErrorByMessageType(msg, errf.instantiateErrorCode(SysErrors.RESOURCE_NOT_FOUND, err));
            return;
        }

        VpnBase base = new VpnBase(vo);
        base.handleMessage(msg);
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
        } else if (msg instanceof APIDownloadVpnCertMsg) {
            handle((APIDownloadVpnCertMsg) msg);
        } else if (msg instanceof APIRenewVpnMsg) {
            handle((APIRenewVpnMsg) msg);
        } else if (msg instanceof APIRenewAutoVpnMsg) {
            handle((APIRenewAutoVpnMsg) msg);
        } else if (msg instanceof APISLAVpnMsg) {
            handle((APISLAVpnMsg) msg);
        } else if (msg instanceof APIGetVpnCertMsg) {
            handle((APIGetVpnCertMsg) msg);
        } else if (msg instanceof APICreateVpnCertMsg) {
            handle((APICreateVpnCertMsg) msg);
        } else if (msg instanceof APIResetVpnCertMsg) {
            handle((APIResetVpnCertMsg) msg);
        } else if (msg instanceof APIUpdateVpnCertMsg) {
            handle((APIUpdateVpnCertMsg) msg);
        } else if (msg instanceof APIChangeVpnCertMsg) {
            handle((APIChangeVpnCertMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIChangeVpnCertMsg msg) {
        APIChangeVpnCertEvent evt = new APIChangeVpnCertEvent(msg.getId());

        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        vo.setVpnCertUuid(msg.getVpnCertUuid());
        final VpnVO vpn = dbf.updateAndRefresh(vo);

        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("change-vpn-cert-%s", msg.getUuid()));
        chain.then(new NoRollbackFlow() {
            String __name__ = "push-vpn-cert";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
// todo
                PushCertMsg pushCertMsg = new PushCertMsg();

                bus.makeLocalServiceId(pushCertMsg, VpnConstant.SERVICE_ID);
                bus.send(pushCertMsg, new CloudBusCallBack(trigger) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            trigger.next();
                        } else {
                            trigger.fail(errf.stringToOperationError("push vpn cert failed!"));
                        }
                    }
                });
            }
        }).then(new NoRollbackFlow() {
            String __name__ = "start-vpn-service";

            @Override
            public void run(final FlowTrigger trigger, Map data) {


            }
        });

        chain.done(new FlowDoneHandler(msg) {
            @Override
            public void handle(Map data) {
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(msg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.instantiateErrorCode(SysErrors.DELETE_RESOURCE_ERROR, errCode));
                bus.publish(evt);
            }
        }).start();
    }


    private void handle(APIDownloadVpnCertMsg msg) {
        // todo

        final APIDownloadVpnCertEvent evt = new APIDownloadVpnCertEvent(msg.getId());
        VpnCertVO vpnCert = Q.New(VpnCertVO.class).eq(VpnCertVO_.uuid, msg.getUuid()).find();
        evt.setInventory(VpnCertInventory.valueOf(vpnCert));
        bus.publish(evt);
    }

    private void handle(APIGetVpnCertMsg msg) {
        APIGetVpnCertReply reply = new APIGetVpnCertReply();
        String vpnCertUuid = Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).select(VpnVO_.vpnCertUuid).findValue();
        if (vpnCertUuid == null) {
            throw new VpnServiceException(
                    operr("The vpn[uuid:%s] has no cert.", msg.getUuid()));
        }
        VpnCertVO vpnCert = Q.New(VpnCertVO.class).eq(VpnCertVO_.uuid, vpnCertUuid).find();
        reply.setInventory(VpnCertInventory.valueOf(vpnCert));
        bus.reply(msg, reply);
    }

    private void handle(APIGetVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        VpnInventory inventory = VpnInventory.valueOf(vpn);

        APIGetVpnReply reply = new APIGetVpnReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);
    }

    private void handle(APIGetVpnPriceMsg msg) {
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        bus.reply(msg, new APIGetVpnPriceReply(reply));
    }

    private String generateCertKey(String accountUuid, String sid) {
        return DigestUtils.md5Hex(accountUuid + sid + VpnConstant.GENERATE_KEY);
    }

    private void doAddVpn(final APICreateVpnMsg msg, ReturnValueCompletion<VpnInventory> completion) {
        VpnHostVO host = dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class);
        checkHostState(host);

        VpnVO vo = new VpnVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setDescription(msg.getDescription());
        vo.setHostUuid(msg.getHostUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setInterfaceUuid(msg.getInterfaceUuid());
        vo.setPort(generatePort(host));
        vo.setVlan(msg.getVlan());
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setDuration(msg.getDuration());
        vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));
        vo.setPayment(Payment.UNPAID);
        vo.setState(VpnState.Enabled);
        vo.setStatus(VpnStatus.Connecting);
        vo.setMaxModifies(VpnGlobalProperty.VPN_MAX_MOTIFIES);
        vo.setSid(Platform.getUuid());
        vo.setCertKey(generateCertKey(msg.getAccountUuid(), vo.getSid()));
        vo.setVpnCertUuid(msg.getVpnCertUuid());
        final VpnVO vpn = dbf.persistAndRefresh(vo);

        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("add-vpn-%s", vpn.getUuid()));

        chain.then(new NoRollbackFlow() {
            String __name__ = "pay-before-add-vpn";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
                ProductInfoForOrder productInfoForOrder = createBuyOrderForVPN(vpn, new CreateVpnCallBack());
                productInfoForOrder.setOpAccountUuid(msg.getSession().getAccountUuid());
                orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));
                createOrder(orderMsg, new Completion(trigger) {
                    @Override
                    public void success() {
                        vpn.setPayment(Payment.PAID);
                        dbf.updateAndRefresh(vpn);
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

        }).then(new NoRollbackFlow() {
            String __name__ = "send-init-add-message";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                initVpn(vo, new Completion(trigger) {
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
        }).then(new NoRollbackFlow() {
            String __name__ = "get-certInfo-after-add-vpn";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                if (vpn.getVpnCertUuid() == null) {
                    getCertInfo(vpn.getUuid(), new ReturnValueCompletion<VpnCertInventory>(trigger) {
                        @Override
                        public void success(VpnCertInventory returnValue) {
                            vpn.setVpnCertUuid(returnValue.getUuid());
                            dbf.updateAndRefresh(vpn);
                            trigger.next();
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            trigger.next();
                        }
                    });
                }
            }
        }).done(new FlowDoneHandler(msg) {
            @Override
            public void handle(Map data) {
                VpnInventory inv = VpnInventory.valueOf(dbf.reload(vo));
                logger.debug(String.format("successfully added vpn[name:%s, uuid:%s]", vo.getName(), vo.getUuid()));
                completion.success(inv);

            }
        }).error(new FlowErrorHandler(msg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                logger.debug(String.format("failed to add vpn[name:%s, uuid:%s]", vo.getName(), vo.getUuid()));
                completion.fail(errCode);
            }
        }).start();
    }


    private void handle(APICreateVpnMsg msg) {
        final APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());

        doAddVpn(msg, new ReturnValueCompletion<VpnInventory>(msg) {
            @Override
            public void success(VpnInventory inventory) {
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
        APIUpdateVpnBandwidthEvent evt = new APIUpdateVpnBandwidthEvent(msg.getId());

        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg(getOrderMsgForVPN(vpn, new UpdateVpnBandwidthCallBack()));
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);

        if (!reply.isOrderSuccess()) {
            evt.setError(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "订单操作失败", reply.getError()));
            bus.publish(evt);
            return;
        }
        vpn.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vpn.setStatus(VpnStatus.Disconnected);
        final VpnVO vo = dbf.updateAndRefresh(vpn);
        saveMotifyRecord(msg);

        RateLimitingMsg rateLimitingMsg = new RateLimitingMsg();
        rateLimitingMsg.setVpnPort(vo.getPort().toString());
        BandwidthOfferingVO bandwidth = dbf.findByUuid(vo.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        rateLimitingMsg.setSpeed(String.valueOf(SizeUnit.BYTE.toKiloByte(bandwidth.getBandwidth())));
        bus.makeLocalServiceId(rateLimitingMsg, VpnConstant.SERVICE_ID);
        bus.send(rateLimitingMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    vo.setStatus(VpnStatus.Connected);
                    evt.setInventory(VpnInventory.valueOf(dbf.updateAndRefresh(vo)));
                } else {
                    evt.setError(reply.getError());
                }
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
        APIUpdateVpnStateEvent evt = new APIUpdateVpnStateEvent(msg.getId());

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        VpnState next = msg.getState();
        if (vpn.getState() == next) {
            evt.setInventory(VpnInventory.valueOf(vpn));
            bus.publish(evt);
            return;
        }
        changeVpnStateByAPI(vpn, next, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInventory.valueOf(vpn));
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
        deleteVpnByApiMessage(msg);
    }

    private void deleteVpnByApiMessage(APIDeleteVpnMsg msg) {
        final APIDeleteVpnEvent evt = new APIDeleteVpnEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        VpnInventory vinv = VpnInventory.valueOf(vpn);
        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("delete-vpn-%s", msg.getUuid()));
        chain.then(new NoRollbackFlow() {
            String __name__ = "Unsubcribe-vpn";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg(getOrderMsgForVPN(vpn, new UnsubcribeVpnCallBack()));
                orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                orderMsg.setStartTime(vinv.getCreateDate());
                orderMsg.setExpiredTime(vinv.getExpireDate());
                createOrder(orderMsg, new Completion(trigger) {
                    @Override
                    public void success() {
                        vpn.setState(VpnState.Disabled);
                        vpn.setStatus(VpnStatus.Disconnected);
                        dbf.updateAndRefresh(vpn);
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "退订失败", errorCode));
                    }
                });

            }
        }).then(new NoRollbackFlow() {
            String __name__ = "send-delete-vpn-message";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                deleteVpn(vpn, new Completion(trigger) {
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
        });

        chain.done(new FlowDoneHandler(msg) {
            @Override
            public void handle(Map data) {
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(msg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.instantiateErrorCode(SysErrors.DELETE_RESOURCE_ERROR, errCode));
                bus.publish(evt);
            }
        }).start();
    }


    private void handle(APIResetVpnCertMsg msg) {
        APIResetVpnCertEvent evt = new APIResetVpnCertEvent(msg.getId());
        CreateCertMsg certMsg = new CreateCertMsg();
        certMsg.setVpnCertUuid(msg.getUuid());
        certMsg.setAccountUuid(msg.getSession().getAccountUuid());
        bus.makeLocalServiceId(certMsg, VpnConstant.SERVICE_ID);
        bus.send(certMsg, new CloudBusCallBack(certMsg) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    CreateCertReply certReply = reply.castReply();
                    evt.setInventory(certReply.getInventory());
                    bus.send(evt);
                } else {
                    evt.setError(reply.getError());
                    bus.send(evt);
                }
            }
        });
    }


    private void handle(APIUpdateVpnCertMsg msg) {
        VpnCertVO vpnCert = dbf.findByUuid(msg.getUuid(), VpnCertVO.class);

        if (StringUtils.isEmpty(msg.getName())) {
            vpnCert.setName(msg.getName());
            dbf.updateAndRefresh(vpnCert);
        }
        APIUpdateVpnCertEvent evt = new APIUpdateVpnCertEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APICreateVpnCertMsg msg) {
        APICreateVpnCertEvent evt = new APICreateVpnCertEvent(msg.getId());

        CreateCertMsg certMsg = new CreateCertMsg();
        certMsg.setVpnCertUuid(Platform.getUuid());
        certMsg.setAccountUuid(msg.getSession().getAccountUuid());

        bus.makeLocalServiceId(certMsg, VpnConstant.SERVICE_ID);

        bus.send(certMsg, new CloudBusCallBack(certMsg) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    evt.setInventory(((CreateCertReply) reply).getInventory());
                    bus.publish(evt);
                } else {
                    evt.setError(reply.getError());
                    bus.publish(evt);
                }
            }
        });
    }

    private void handleLocalMessage(Message msg) {
        if (msg instanceof CreateCertMsg) {
            handle((CreateCertMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(final CreateCertMsg msg) {
        thdf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return String.format("craete-cert-%s", msg.getVpnCertUuid());
            }

            @Override
            public void run(final SyncTaskChain chain) {
                CreateCertReply reply = new CreateCertReply();
                CreateCertCmd cmd = new CreateCertCmd();
                VpnHostVO host = Q.New(VpnHostVO.class).limit(1).find();

                String baseUrl = URLBuilder.buildUrlFromBase(String.format("http://%s:%s/", host.getHostIp(),
                        VpnGlobalProperty.AGENT_PORT), VpnConstant.CREATE_CERT_PATH);
                createCert(baseUrl, cmd, new ReturnValueCompletion<CreateCertRsp>(reply) {
                    @Override
                    public void success(CreateCertRsp ret) {
                        VpnCertVO vpnCert = dbf.findByUuid(msg.getVpnCertUuid(), VpnCertVO.class);
                        if (vpnCert == null) {
                            vpnCert = new VpnCertVO();
                            vpnCert.setUuid(msg.getVpnCertUuid());
                            vpnCert.setAccountUuid(msg.getAccountUuid());
                        }
                        vpnCert.setCaCert(ret.ca_crt);
                        vpnCert.setCaCert(ret.ca_key);
                        vpnCert.setClientKey(ret.client_key);
                        vpnCert.setClientCert(ret.client_crt);
                        vpnCert.setServerKey(ret.server_key);
                        vpnCert.setServerCert(ret.server_crt);
                        vpnCert.setDh1024Pem(ret.dh1024_pem);
                        reply.setInventory(VpnCertInventory.valueOf(dbf.updateAndRefresh(vpnCert)));
                        bus.reply(msg, reply);
                        chain.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        reply.setError(errorCode);
                        bus.reply(msg, reply);
                        chain.next();
                    }
                });
            }

            @Override
            public String getName() {
                return "create-cert";
            }

            @Override
            protected int getSyncLevel() {
                return 1;
            }
        });
    }

    private void createCert(String url, final AgentCommand cmd, final ReturnValueCompletion<CreateCertRsp> completion) {

        try {
            CreateCertRsp rsp = restf.syncJsonPost(url, cmd, CreateCertRsp.class);
            if (rsp.isSuccess()) {
                completion.success(rsp);
            } else {
                logger.debug(String.format("ERROR: %s", rsp.getError()));
                completion.fail(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, rsp.getError()));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            completion.fail(errf.instantiateErrorCode(SysErrors.HTTP_ERROR, e.getMessage()));
        }
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

    private final List<String> vpnUuids = Collections.synchronizedList(new ArrayList<String>());

    private class VpnStatusCheckWorker implements PeriodicTask {
        private void handleReply(String vpnUuid, MessageReply reply) {
            if (!reply.isSuccess()) {
                logger.warn(String.format(" unable track vpn[uuid:%s], %s", vpnUuid, reply.getError()));
                return;
            }
            final VpnStatusReply r = reply.castReply();
            boolean needReconnect = false;
            if (r.isConnected() && r.getCurrentStatus() == VpnStatus.Disconnected) {
                needReconnect = true;
            } else if (!r.isConnected() && r.getCurrentStatus() == VpnStatus.Connected) {
                needReconnect = true;
            } else if (!r.isConnected() && r.getCurrentStatus() == VpnStatus.Disconnected) {
                needReconnect = true;
            }
            if (needReconnect) {
//todo restart vpn service
            }

        }

        @Override
        public void run() {
            try {
                List<VpnStatusMsg> msgs;
                synchronized (vpnUuids) {
                    msgs = new ArrayList<>();
                    for (String vpnUuid : vpnUuids) {
                        VpnStatusMsg msg = new VpnStatusMsg();
                        msg.setVpnUuid(vpnUuid);
                        bus.makeLocalServiceId(msg, VpnConstant.SERVICE_ID);
                        msgs.add(msg);
                    }
                }

                logger.debug("start check vpn status.");
                if (msgs.isEmpty()) {
                    return;
                }
                bus.send(msgs, HostGlobalProperty.HOST_TRACK_PARALLELISM_DEGREE, new CloudBusSteppingCallback(null) {
                    @Override
                    public void run(NeedReplyMessage msg, MessageReply reply) {
                        VpnStatusMsg vmsg = (VpnStatusMsg) msg;
                        handleReply(vmsg.getVpnUuid(), reply);
                    }
                });
            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
            }
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
                            initVpn(vpn, new Completion(message) {
                                @Override
                                public void success() {
                                    logger.debug("successfully callback from billing to init vpn;");
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                    logger.debug("callback from billingm, failed to init vpn;");
                                }
                            });
                        }
                        return null;
                    } else if (message instanceof RenewAutoVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof RenewVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof SlaVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof UnsubcribeVpnCallBack) {
                        VpnVO vpn = dbf.findByUuid(cmd.getPorductUuid(), VpnVO.class);
                        if (vpn != null) {
                            deleteVpn(vpn, new Completion(message) {
                                @Override
                                public void success() {
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                    logger.debug(String.format("delete vpn failed, cause by: %s", errorCode.getDetails()));
                                }
                            });
                        }
                    } else if (message instanceof UpdateVpnBandwidthCallBack) {
                        VpnVO vpn = updateVpnFromOrder(cmd);
                        updateMotifyRecord(cmd);
                        if (vpn != null && vpn.getStatus() == VpnStatus.Disconnected)
                            reconnectVpn(vpn);
                    } else {
                        logger.debug("未知回调！！！！！！！！");
                    }
                    return null;
                });
        return true;
    }

    private void updateMotifyRecord(OrderCallbackCmd cmd) {
        VpnMotifyRecordVO record = dbf.getEntityManager().find(VpnMotifyRecordVO.class, cmd.getPorductUuid());
        record.setMotifyType(cmd.getType().toString());
        dbf.update(record);
    }

    private VpnVO updateVpnFromOrder(OrderCallbackCmd cmd) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, cmd.getPorductUuid());
        if (vpn == null) {
            return null;
        }
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
        } else if (msg instanceof APIDownloadVpnCertMsg) {
            validate((APIDownloadVpnCertMsg) msg);
        } else if (msg instanceof APIUpdateVpnBandwidthMsg) {
            validate((APIUpdateVpnBandwidthMsg) msg);
        } else if (msg instanceof APIGetVpnCertMsg) {
            validate((APIGetVpnCertMsg) msg);
        }
        return msg;
    }


    private void validate(APIDownloadVpnCertMsg msg) {

    }

    private void validate(APIGetVpnCertMsg msg) {

        VpnVO vpn = Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).find();

        String md5 = DigestUtils.md5Hex(msg.getUuid() + msg.getTimestamp() + vpn.getCertKey());
        logger.debug(String.format("MD5[%s] && signature[%s]", md5, msg.getSignature()));
        boolean flag = System.currentTimeMillis() - msg.getTimestamp() > CoreGlobalProperty.INNER_MESSAGE_EXPIRE * 1000;
        if (!md5.equals(msg.getSignature()) || flag) {
            throw new ApiMessageInterceptionException(errf.stringToInvalidArgumentError(
                    String.format("The parameters of the message[%s] are inconsistent ", msg.getMessageName())
            ));
        }
    }

    private void validate(APIUpdateVpnBandwidthMsg msg) {
        LocalDateTime dateTime = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(VpnMotifyRecordVO.class)
                .eq(VpnMotifyRecordVO_.vpnUuid, msg.getUuid())
                .gte(VpnMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime))
                .count();
        Integer maxModifies = Q.New(VpnVO.class)
                .eq(VpnVO_.uuid, msg.getUuid())
                .select(VpnVO_.maxModifies)
                .findValue();

        if (times >= maxModifies) {
            throw new OperationFailureException(
                    operr("The Vpn[uuid:%s] has motified %s times.", msg.getUuid(), times));
        }
    }


    private void validate(APIUpdateVpnMsg msg) {
        if (!msg.getSession().isAdminSession() && msg.getMaxModifies() != null) {
            throw new ApiMessageInterceptionException(
                    argerr("The account[name:%s] has no permission modify.", msg.getName()));
        }

        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName()).notEq(VpnVO_.uuid, msg.getUuid());
        if (q.isExists())
            throw new ApiMessageInterceptionException(
                    argerr("The Vpn[name:%s] is already exist.", msg.getName()));
    }

    private void validate(APIQueryVpnMsg msg) {
        if (!msg.getSession().isAdminSession()) {
            msg.addQueryCondition(VpnVO_.accountUuid.toString(), QueryOp.EQ, msg.getSession().getAccountUuid());
        }
    }

    private void validate(APICreateVpnMsg msg) {
        // 区分管理员账户
        if (msg.getSession().isAdminSession() && StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] is not a admin or proxy.",
                            msg.getSession().getAccountUuid()));
        }
        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName()).eq(VpnVO_.accountUuid, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("The name[%s] of the vpn can not repeat.", msg.getName()));
        }
        // 物理机
        String hostUuid = Q.New(HostInterfaceVO.class)
                .eq(HostInterfaceVO_.interfaceUuid, msg.getInterfaceUuid())
                .select(HostInterfaceVO_.hostUuid).findValue();

        if (hostUuid == null) {
            throw new ApiMessageInterceptionException(
                    argerr("The host of the interface[uuid:%s] does not exist.", msg.getInterfaceUuid()));
        }
        q = Q.New(VpnVO.class).eq(VpnVO_.hostUuid, hostUuid).eq(VpnVO_.vlan, msg.getVlan());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("The vlan[%s] of the host[uuid:%s] is already exist.", msg.getVlan(), hostUuid));
        }
        msg.setHostUuid(hostUuid);
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }

    private boolean reconnectVpn(VpnVO vo) {
        checkState(vo);
        initVpn(vo, new Completion(null) {
            @Override
            public void success() {
                logger.debug(String.format("reconnect vpn[UUID: %s]", vo.getUuid()));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.debug(String.format("ERROR[%s]: %s", errorCode.getCode(), errorCode.getDetails()));
            }
        });
        return true;
    }

    private void deleteVpn(VpnVO vo, final Completion complete) {
        DestroyVpnMsg destroyVpnMsg = new DestroyVpnMsg();
        destroyVpnMsg.setVpnUuid(vo.getUuid());
        destroyVpnMsg.setVpnPort(vo.getPort().toString());
        destroyVpnMsg.setVpnVlan(vo.getVlan().toString());
        destroyVpnMsg.setInterfaceName(vo.getVpnHost().getInterfaceName());
        bus.makeLocalServiceId(destroyVpnMsg, VpnConstant.SERVICE_ID);
        bus.send(destroyVpnMsg, new CloudBusCallBack(complete) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    complete.success();
                } else {
                    complete.fail(reply.getError());
                }
            }
        });
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
                logger.debug(String.format("Message[%s]:交易失败", orderMsg.getClass().getSimpleName()));
                complete.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "交易失败"));
            }
        } catch (Exception e) {
            logger.debug(String.format("call billing[url: %s] failed.", url));
            complete.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, String.format("call billing[url: %s] failed.", url)));
        }
    }

    private void initVpn(VpnVO vo, final Completion complete) {
        InitVpnMsg initVpnMsg = InitVpnMsg.valueOf(vo);
        BandwidthOfferingVO bandwidth = dbf.findByUuid(vo.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        initVpnMsg.setSpeed(String.valueOf(SizeUnit.BYTE.toKiloByte(bandwidth.getBandwidth())));
        initVpnMsg.setInterfaceName(vo.getVpnHost().getInterfaceName());

        bus.makeLocalServiceId(initVpnMsg, VpnConstant.SERVICE_ID);
        bus.send(initVpnMsg, new CloudBusCallBack(complete) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    VpnVO vpn = dbf.reload(vo);
                    InitVpnReply initVpnReply = reply.castReply();
                    if (vpn.getStatus() != VpnStatus.valueOf(initVpnReply.getStatus())) {
                        vpn.setStatus(VpnStatus.valueOf(initVpnReply.getStatus()));
                        dbf.updateAndRefresh(vpn);
                    }
                    complete.success();
                } else {
                    complete.fail(reply.getError());
                }
            }
        });
    }

    private void getCertInfo(String vpnUuid, final ReturnValueCompletion<VpnCertInventory> complete) {
        ClientInfoMsg msg = new ClientInfoMsg();
        msg.setVpnUuid(vpnUuid);
        bus.makeLocalServiceId(msg, VpnConstant.SERVICE_ID);
        bus.send(msg, new CloudBusCallBack(complete) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    ClientInfoReply infoReply = reply.castReply();
                    complete.success(infoReply.getInventory());
                } else {
                    complete.fail(reply.getError());
                }
            }
        });
    }

    private void changeVpnStateByAPI(VpnVO vpn, VpnState next, final Completion complete) {
        ChangeVpnStateMsg changeVpnStateMsg = new ChangeVpnStateMsg();
        changeVpnStateMsg.setVpnUuid(vpn.getUuid());
        changeVpnStateMsg.setCurrentState(vpn.getState());
        changeVpnStateMsg.setState(next);
        bus.makeLocalServiceId(changeVpnStateMsg, VpnConstant.SERVICE_ID);
        bus.send(changeVpnStateMsg, new CloudBusCallBack(complete) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    changeVpnState(vpn, next);
                    complete.success();
                } else {
                    complete.fail(reply.getError());
                }
            }
        });
    }

    private void changeVpnState(VpnVO vpn, VpnState next) {
        VpnState currentState = vpn.getState();
        vpn.setState(next);
        vpn = dbf.updateAndRefresh(vpn);
        logger.debug(String.format("Vpn[%s]'s state changed from %s to %s", vpn.getUuid(), currentState, vpn.getState()));
    }

    private void checkState(VpnVO vo) {
        checkHostState(vo.getVpnHost());
        checkVpnState(vo);
    }

    private void checkHostState(VpnHostVO vo) {
        if (HostState.Disabled == vo.getState()) {
            throw new OperationFailureException(operr("unable to do the operation " +
                    "because the host is in state of Disabled"));
        }
    }

    private void checkVpnState(VpnVO vo) {
        if (VpnState.Disabled == vo.getState()) {
            throw new OperationFailureException(operr("vpn[uuid:%s, name:%s] is in state[%s], " +
                    "cannot perform required operation", vo.getUuid(), vo.getName(), vo.getState()));
        }
    }


}
