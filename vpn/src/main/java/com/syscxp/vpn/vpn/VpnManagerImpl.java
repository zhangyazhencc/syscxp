package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.ansible.AnsibleConstant;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.CloudBusListCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.*;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.configuration.MotifyType;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.configuration.ResourceMotifyRecordVO_;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.host.HostState;
import com.syscxp.header.host.HostStatus;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.billingCallBack.*;
import com.syscxp.header.vpn.host.APIDeleteHostInterfaceEvent;
import com.syscxp.header.vpn.host.VpnHostConstant;
import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.exception.VpnServiceException;
import com.syscxp.vpn.job.DestroyVpnJob;
import com.syscxp.vpn.quota.VpnQuotaOperator;
import com.syscxp.vpn.vpn.VpnCommands.AgentCommand;
import com.syscxp.vpn.vpn.VpnCommands.AgentResponse;
import com.syscxp.vpn.vpn.VpnCommands.VpnStatusCmd;
import com.syscxp.vpn.vpn.VpnCommands.VpnStatusRsp;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;
import static com.syscxp.utils.CollectionDSL.list;

/**
 * @author wangjie
 */
@Component
public class VpnManagerImpl extends AbstractService implements VpnManager, ApiMessageInterceptor, ReportQuotaExtensionPoint {
    private static final CLogger LOGGER = Utils.getLogger(VpnManagerImpl.class);

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
            String err = "Cannot find vpn: " + msg.getVpnUuid() + ", it may have been deleted";
            bus.replyErrorByMessageType(msg, err);
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
        } else if (msg instanceof APIDeleteVpnCertMsg) {
            handle((APIDeleteVpnCertMsg) msg);
        } else if (msg instanceof APIGetVpnMsg) {
            handle((APIGetVpnMsg) msg);
        } else if (msg instanceof APIGetVpnPriceMsg) {
            handle((APIGetVpnPriceMsg) msg);
        } else if (msg instanceof APIUpdateVpnStateMsg) {
            handle((APIUpdateVpnStateMsg) msg);
        } else if (msg instanceof APIGenerateDownloadUrlMsg) {
            handle((APIGenerateDownloadUrlMsg) msg);
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
        } else if (msg instanceof APIAttachVpnCertMsg) {
            handle((APIAttachVpnCertMsg) msg);
        } else if (msg instanceof APIDetachVpnCertMsg) {
            handle((APIDetachVpnCertMsg) msg);
        } else if (msg instanceof APIResetVpnCertKeyMsg) {
            handle((APIResetVpnCertKeyMsg) msg);
        } else if (msg instanceof APIListSupportedEndpointMsg) {
            handle((APIListSupportedEndpointMsg) msg);
        } else if (msg instanceof APIGetRenewVpnPriceMsg) {
            handle((APIGetRenewVpnPriceMsg) msg);
        } else if (msg instanceof APIGetUnscribeVpnPriceDiffMsg) {
            handle((APIGetUnscribeVpnPriceDiffMsg) msg);
        } else if (msg instanceof APIGetModifyVpnPriceDiffMsg) {
            handle((APIGetModifyVpnPriceDiffMsg) msg);
        } else if (msg instanceof APIDestroyVpnMsg) {
            handle((APIDestroyVpnMsg) msg);
        } else if (msg instanceof APIGetVpnModifyMsg) {
            handle((APIGetVpnModifyMsg) msg);
        } else if (msg instanceof APICheckVpnForTunnelMsg) {
            handle((APICheckVpnForTunnelMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICheckVpnForTunnelMsg msg) {
        APICheckVpnForTunnelReply reply = new APICheckVpnForTunnelReply();
        Q q = Q.New(VpnVO.class).eq(VpnVO_.tunnelUuid, msg.getTunnelUuid());
        reply.setUsed(q.isExists());
        bus.reply(msg, reply);
    }

    private void handle(APIGetVpnModifyMsg msg) {
        APIGetVpnModifyReply reply = new APIGetVpnModifyReply();

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        Long modifies = Q.New(ResourceMotifyRecordVO.class)
                .eq(ResourceMotifyRecordVO_.resourceUuid, msg.getUuid())
                .count();
        reply.setMaxModifies(vpn.getMaxModifies());
        reply.setModifies(Math.toIntExact(modifies));

        bus.reply(msg, reply);
    }

    private void handle(APIDestroyVpnMsg msg) {
        APIDestroyVpnReply reply = new APIDestroyVpnReply();
        List<String> vpnUuids = Q.New(VpnVO.class)
                .eq(VpnVO_.tunnelUuid, msg.getTunnelUuid())
                .select(VpnVO_.uuid)
                .listValues();

        if (vpnUuids.isEmpty()) {
            bus.reply(msg, reply);
            return;
        }

        UpdateQuery.New(VpnVO.class)
                .in(VpnVO_.uuid, vpnUuids)
                .set(VpnVO_.tunnelUuid, "")
                .set(VpnVO_.state, VpnState.Disabled)
                .update();

        for (String vpnUuid : vpnUuids) {
            DestroyVpnJob destroyVpnJob = new DestroyVpnJob();
            destroyVpnJob.setVpnUuid(vpnUuid);
            jobf.execute("销毁VPN服务", Platform.getManagementServerId(), destroyVpnJob);
        }
        bus.reply(msg, reply);

    }

    private void handle(APIDeleteVpnCertMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnCertVO.class);
        APIDeleteHostInterfaceEvent evt = new APIDeleteHostInterfaceEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIGetModifyVpnPriceDiffMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APIGetModifyProductPriceDiffMsg pmsg = new APIGetModifyProductPriceDiffMsg();
        pmsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid(), vo.getEndpointUuid()));
        pmsg.setProductUuid(msg.getUuid());
        pmsg.setAccountUuid(vo.getAccountUuid());
        pmsg.setExpiredTime(vo.getExpireDate());

        APIGetModifyProductPriceDiffReply reply = createOrder(pmsg);
        bus.reply(msg, new APIGetModifyVpnPriceDiffReply(reply));
    }

    private void handle(APIGetUnscribeVpnPriceDiffMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();

        upmsg.setAccountUuid(vo.getAccountUuid());
        upmsg.setProductUuid(msg.getUuid());
        upmsg.setExpiredTime(vo.getExpireDate());

        APIGetUnscribeProductPriceDiffReply reply = createOrder(upmsg);

        bus.reply(msg, new APIGetUnscribeVpnPriceDiffReply(reply));
    }

    private void handle(APIGetRenewVpnPriceMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        APIGetRenewProductPriceMsg rpmsg = new APIGetRenewProductPriceMsg();

        rpmsg.setAccountUuid(vo.getAccountUuid());
        rpmsg.setProductUuid(msg.getUuid());
        rpmsg.setDuration(msg.getDuration());
        rpmsg.setProductChargeModel(msg.getProductChargeModel());

        APIGetRenewProductPriceReply reply = createOrder(rpmsg);

        bus.reply(msg, new APIGetRenewInterfacePriceReply(reply));
    }

    private void handle(APIListSupportedEndpointMsg msg) {
        APIListSuppoetedEndpointReply reply = new APIListSuppoetedEndpointReply();

        APIQueryTunnelMsg tunnelMsg = new APIQueryTunnelMsg();
        tunnelMsg.addQueryCondition("uuid", "=", msg.getTunnelUuid());
        tunnelMsg.setSession(msg.getSession());

        String url = URLBuilder.buildUrlFromBase(VpnGlobalProperty.TUNNEL_SERVER_RUL, RESTConstant.REST_API_CALL);
        TunnelInventory inventory;
        try {
            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dumpWithSession(tunnelMsg), RestAPIResponse.class);
            APIReply apiReply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (!reply.isSuccess() && ((APIQueryTunnelReply) apiReply).getInventories().isEmpty()) {
                throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.RESOURCE_NOT_FOUND,
                        String.format("专线[uuid:%s]找不到.", msg.getTunnelUuid())));
            }
            inventory = ((APIQueryTunnelReply) apiReply).getInventories().get(0);

        } catch (Exception e) {
            throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.HTTP_ERROR, String.format("调用tunnel服务[url: %s]失败.", url)));
        }

        List<APIListSuppoetedEndpointReply.SupportedEndpointInventory> inventories = new ArrayList<>();

        for (TunnelSwitchPortInventory switchPortInventory : inventory.getTunnelSwitchs()) {

            String sql = "select vh.uuid from VpnHostVO vh, HostInterfaceVO hi where vh.uuid = hi.hostUuid " +
                    "and hi.endpointUuid = :endpointUuid and vh.status = :status";

            List<String> uuids = SQL.New(sql)
                    .param("endpointUuid", switchPortInventory.getEndpointUuid())
                    .param("status", HostStatus.Connected)
                    .list();
            if (!CollectionUtils.isEmpty(uuids)) {
                APIListSuppoetedEndpointReply.SupportedEndpointInventory inv = new APIListSuppoetedEndpointReply.SupportedEndpointInventory();
                inv.setUuid(switchPortInventory.getEndpointUuid());
                inv.setName(switchPortInventory.getEndpoint().getName());
                inv.setVlan(switchPortInventory.getVlan());
                inventories.add(inv);
            }
        }
        reply.setInventories(inventories);
        bus.reply(msg, reply);
    }

    private void handle(APIResetVpnCertKeyMsg msg) {
        final APIResetVpnCertKeyEvent event = new APIResetVpnCertKeyEvent(msg.getId());
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setSid(Platform.getUuid());
        vpn.setCertKey(generateCertKey(vpn.getAccountUuid(), vpn.getSid()));
        event.setInventory(VpnInventory.valueOf(dbf.updateAndRefresh(vpn)));
        bus.publish(event);
    }


    private void handle(APIGenerateDownloadUrlMsg msg) {

        final APIGenerateDownloadUrlReply reply = new APIGenerateDownloadUrlReply();
        String type = "cert".equals(msg.getType()) ? VpnCertVO.class.getSimpleName() : VpnVO.class.getSimpleName();
        String accountUuid = SQL.New(String.format("select r.accountUuid from %s r where r.uuid = :uuid ", type))
                .param("uuid", msg.getUuid()).find();
        if (accountUuid != null) {
            StringBuilder sb = new StringBuilder();
            long time = System.currentTimeMillis();
            sb.append(msg.getUuid()).append(":").append(time);
            sb.append(":").append(DigestUtils.md5Hex(accountUuid + time + VpnConstant.URL_GENERATE_KEY));

            String path = new String(Base64.encode(sb.toString().getBytes()));

            reply.setDownloadUrl(URLBuilder.buildUrlFromBase(restf.getBaseUrl(), RESTConstant.REST_API_CALL, "/", msg.getType(), "/", path));
        }
        bus.reply(msg, reply);
    }

    private void handle(APIGetVpnCertMsg msg) {
        APIGetVpnCertReply reply = new APIGetVpnCertReply();
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        reply.setInventory(ClientConfInventory.valueOf(vpn));
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
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid(), msg.getEndpointUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        bus.reply(msg, new APIGetVpnPriceReply(reply));
    }

    private String generateCertKey(String accountUuid, String sid) {
        return DigestUtils.md5Hex(accountUuid + sid + VpnConstant.URL_GENERATE_KEY);
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
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());
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
        dbf.persistAndRefresh(vo);
        LOGGER.debug(String.format("数据库保存VPN[name:%s, uuid:%s]成功", vo.getName(), vo.getUuid()));

        attachVpnCert(vo.getUuid(), msg.getVpnCertUuid());

        final VpnVO vpn = dbf.findByUuid(vo.getUuid(), VpnVO.class);

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
                VpnInventory inv = VpnInventory.valueOf(dbf.reload(vo));
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
        order.setDescriptionData(getDescriptionForVPN(vo.getBandwidthOfferingUuid()));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setAccountUuid(vo.getAccountUuid());
        order.setUnits(generateUnits(vo.getBandwidthOfferingUuid(), vo.getEndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    private APICreateOrderMsg getOrderMsgForVPN(VpnVO vo, String bandwidthOfferingUuid, NotifyCallBackData callBack) {
        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setDescriptionData(getDescriptionForVPN(bandwidthOfferingUuid));
        orderMsg.setAccountUuid(vo.getAccountUuid());
        orderMsg.setUnits(generateUnits(bandwidthOfferingUuid, vo.getEndpointUuid()));
        orderMsg.setCallBackData(RESTApiDecoder.dump(callBack));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        return orderMsg;
    }

    private String getDescriptionForVPN(String bandwidthOfferingUuid) {
        DescriptionData data = new DescriptionData();
        data.add(new DescriptionItem("带宽", bandwidthOfferingUuid));
        return JSONObjectUtil.toJsonString(data);
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

    @Deferred
    private Integer generatePort(VpnHostVO host) {
        GLock lock = new GLock("VpnPort", TimeUnit.MINUTES.toSeconds(2));
        lock.lock();
        Defer.defer(lock::unlock);

        Integer port = Q.New(VpnVO.class).eq(VpnVO_.hostUuid, host.getUuid())
                .orderBy(VpnVO_.port, SimpleQuery.Od.DESC)
                .select(VpnVO_.port)
                .limit(1).findValue();
        if (port == null) {
            return host.getStartPort();
        }
        if (port >= host.getEndPort()) {
            throw new VpnServiceException(
                    argerr("物理机[uuid:%s]没有可用的端口.", host.getUuid()));

        }
        return port + 1;
    }

    private void handle(APIRenewVpnMsg msg) {
        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(getOrderMsgForVPN(vo, vo.getBandwidthOfferingUuid(), new RenewVpnCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
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
        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(getOrderMsgForVPN(vo, vo.getBandwidthOfferingUuid(), new RenewAutoVpnCallBack()));
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
        APICreateSLACompensationOrderMsg orderMsg = new APICreateSLACompensationOrderMsg(getOrderMsgForVPN(vo, vo.getBandwidthOfferingUuid(), new SlaVpnCallBack()));
        orderMsg.setSlaUuid(msg.getSlaUuid());
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());


        APICreateOrderReply reply = createOrder(orderMsg);
        afterRenewVpn(reply, vo, msg);
    }

    private void saveMotifyRecord(APIUpdateVpnBandwidthMsg msg, MotifyType type) {
        ResourceMotifyRecordVO record = new ResourceMotifyRecordVO();
        record.setResourceUuid(msg.getUuid());
        record.setResourceType(VpnVO.class.getSimpleName());
        record.setUuid(Platform.getUuid());
        record.setMotifyType(type);
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setOpUserUuid(msg.getSession().getUserUuid());
        dbf.persistAndRefresh(record);
    }

    public void handle(APIUpdateVpnBandwidthMsg msg) {
        APIUpdateVpnBandwidthEvent evt = new APIUpdateVpnBandwidthEvent(msg.getId());

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        if (msg.getBandwidthOfferingUuid().equals(vpn.getBandwidthOfferingUuid())) {
            evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
            bus.publish(evt);
            return;
        }

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg(getOrderMsgForVPN(vpn, msg.getBandwidthOfferingUuid(), new UpdateVpnBandwidthCallBack()));
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(vpn.getCreateDate());
        orderMsg.setExpiredTime(vpn.getExpireDate());

        APICreateOrderReply reply = createOrder(orderMsg);

        if (!reply.isOrderSuccess()) {
            evt.setError(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "订单操作失败", reply.getError()));
            bus.publish(evt);
            return;
        }
        motifyRecordVpn(msg, MotifyType.valueOf(reply.getInventory().getType()));

        RateLimitingMsg rateLimitingMsg = new RateLimitingMsg();
        rateLimitingMsg.setVpnUuid(vpn.getUuid());
        bus.makeLocalServiceId(rateLimitingMsg, VpnConstant.SERVICE_ID);
        bus.send(rateLimitingMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
                } else {
                    evt.setError(reply.getError());
                }
                bus.publish(evt);
            }
        });
    }

    @Transactional
    private void motifyRecordVpn(APIUpdateVpnBandwidthMsg msg, MotifyType type) {
        VpnVO vpn = dbf.getEntityManager().find(VpnVO.class, msg.getUuid());
        vpn.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        dbf.getEntityManager().merge(vpn);

        ResourceMotifyRecordVO record = new ResourceMotifyRecordVO();
        record.setResourceUuid(msg.getUuid());
        record.setResourceType(VpnVO.class.getSimpleName());
        record.setUuid(Platform.getUuid());
        record.setMotifyType(type);
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setOpUserUuid(msg.getSession().getUserUuid());
        dbf.getEntityManager().persist(record);
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
        if (update) {
            vpn = dbf.getEntityManager().merge(vpn);
        }
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    public void handle(APIUpdateVpnStateMsg msg) {
        APIUpdateVpnStateEvent evt = new APIUpdateVpnStateEvent(msg.getId());

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        changeVpnStateByAPI(vpn, msg.getState(), new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
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
                if (vpn.getAccountUuid() != null) {
                    APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg(getOrderMsgForVPN(vpn, vpn.getBandwidthOfferingUuid(), new UnsubcribeVpnCallBack()));
                    orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                    orderMsg.setStartTime(vinv.getCreateDate());
                    orderMsg.setExpiredTime(vinv.getExpireDate());
                    createOrder(orderMsg, new Completion(trigger) {
                        @Override
                        public void success() {
                            vpn.setAccountUuid(null);
                            vpn.setState(VpnState.Disabled);
                            vpn.setExpireDate(dbf.getCurrentSqlTime());
                            dbf.updateAndRefresh(vpn);
                            LOGGER.debug(String.format("VPN[UUID:%s] 退订成功", vpn.getUuid()));
                            trigger.next();
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            LOGGER.debug(String.format("VPN[UUID:%s] 退订失败", vpn.getUuid()));
                            trigger.fail(errf.instantiateErrorCode(VpnErrors.CALL_BILLING_ERROR, "退订失败", errorCode));
                        }
                    });
                } else {
                    trigger.next();
                }
            }
        }).then(new NoRollbackFlow() {
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
            String __name__ = "send-delete-vpn-message";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                LOGGER.debug(String.format("创建VPN[UUID:%s]销毁任务", vpn.getUuid()));
                DestroyVpnJob destroyVpnJob = new DestroyVpnJob();
                destroyVpnJob.setVpnUuid(vpn.getUuid());
                destroyVpnJob.setDelete(true);
                jobf.execute("销毁VPN服务", Platform.getManagementServerId(), destroyVpnJob);
                trigger.next();
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

    private void handle(APIUpdateVpnCertMsg msg) {
        VpnCertVO vpnCert = dbf.findByUuid(msg.getUuid(), VpnCertVO.class);

        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            vpnCert.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            vpnCert.setDescription(msg.getDescription());
            update = true;
        }
        if (update) {
            dbf.update(vpnCert);
        }
        APIUpdateVpnCertEvent evt = new APIUpdateVpnCertEvent(msg.getId());
        evt.setInventory(VpnCertInventory.valueOf(dbf.reload(vpnCert)));
        bus.publish(evt);
    }

    private void handle(APICreateVpnCertMsg msg) {
        APICreateVpnCertEvent evt = new APICreateVpnCertEvent(msg.getId());
        VpnCertVO vo = new VpnCertVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setDescription(msg.getDescription());
        vo.setVpnNum(0);
        vo.setVersion(0);
        createCert(vo, new Completion(evt) {
            @Override
            public void success() {
                evt.setInventory(VpnCertInventory.valueOf(dbf.reload(vo)));
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });
    }

    private String ERROR = "ERROR";

    private void createCert(VpnCertVO vpnCert, Completion completion) {
        try {
            String output = ShellUtils.run(String.format("PYTHONPATH=%s %s %s -d '%s' ",
                    AnsibleConstant.ROOT_DIR, "python", VpnHostConstant.CREATE_CERT_PATH, AnsibleConstant.ROOT_DIR),
                    AnsibleConstant.ROOT_DIR);
            ShellUtils.run(String.format("chmod -R 755 %s", VpnHostConstant.EASY_RSA_PATH), AnsibleConstant.ROOT_DIR);
            LOGGER.debug(String.format("run command: python create_cert.py %s, output: %s", AnsibleConstant.ROOT_DIR, output));
            if (output.contains(ERROR)) {
                completion.fail(operr(output));
            }

            vpnCert.setCaCert(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.CA_CRT_PATH)));
            vpnCert.setCaKey(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.CA_KEY_PATH)));
            vpnCert.setClientCert(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.CLIENT_CRT_PATH)));
            vpnCert.setClientKey(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.CLIENT_KEY_PATH)));
            vpnCert.setServerCert(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.SERVER_CRT_PATH)));
            vpnCert.setServerKey(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.SERVER_KEY_PATH)));
            vpnCert.setDh1024Pem(FileUtils.readFileToString(new File(VpnHostConstant.EASY_RSA_PATH, VpnConstant.DH1024_PEM_PATH)));
            vpnCert.setVersion(vpnCert.getVersion() + 1);

            dbf.updateAndRefresh(vpnCert);
            completion.success();
        } catch (Exception se) {
            LOGGER.debug(se.getMessage(), se);
            completion.fail(errf.instantiateErrorCode(VpnErrors.CREATE_CERT_ERRORS, "create cert failed"));
        }
    }


    private void handle(APIResetVpnCertMsg msg) {
        APIResetVpnCertEvent evt = new APIResetVpnCertEvent(msg.getId());

        final VpnCertVO vpnCert = dbf.findByUuid(msg.getUuid(), VpnCertVO.class);

        FlowChain chain = FlowChainBuilder.newSimpleFlowChain();
        chain.setName(String.format("reset-vpn-cert-%s", msg.getUuid()));
        chain.then(new NoRollbackFlow() {
            String __name__ = "reset-vpn-cert";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                createCert(vpnCert, new Completion(trigger) {
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
            String __name__ = "push-vpn-cert";

            @Override
            public void run(final FlowTrigger trigger, Map data) {

                List<String> vpnUuids = Q.New(VpnVO.class)
                        .eq(VpnVO_.vpnCertUuid, vpnCert.getUuid())
                        .select(VpnVO_.uuid)
                        .listValues();
                if (vpnUuids.isEmpty()) {
                    trigger.next();
                    return;
                }

                List<PushCertMsg> msgs = new ArrayList<>();
                for (String vpnUuid : vpnUuids) {
                    PushCertMsg pushCertMsg = new PushCertMsg();
                    pushCertMsg.setVpnUuid(vpnUuid);
                    bus.makeLocalServiceId(pushCertMsg, VpnConstant.SERVICE_ID);
                    msgs.add(pushCertMsg);
                }

                bus.send(msgs, new CloudBusListCallBack(trigger) {
                    @Override
                    public void run(List<MessageReply> replies) {
                        for (MessageReply reply : replies) {
                            if (!reply.isSuccess()) {
                                trigger.fail(reply.getError());
                                break;
                            }
                            trigger.next();
                        }
                    }
                });
            }
        });

        chain.done(new FlowDoneHandler(msg) {
            @Override
            public void handle(Map data) {
                evt.setInventory(VpnCertInventory.valueOf(dbf.reload(vpnCert)));
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(msg) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errCode);
                bus.publish(evt);
            }
        }).start();
    }

    private void handle(APIDetachVpnCertMsg msg) {
        APIDetachVpnCertEvent evt = new APIDetachVpnCertEvent(msg.getId());

        VpnVO vo = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        changeVpnStateByAPI(vo, VpnState.Disabled, new Completion(evt) {
            @Override
            public void success() {

                detachVpnCert(msg.getUuid(), vo.getVpnCertUuid());

                evt.setInventory(VpnInventory.valueOf(dbf.findByUuid(vo.getUuid(), VpnVO.class)));
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
    private void detachVpnCert(String vpnUuid, String vpnCertUuid) {
        VpnVO vpnVO = dbf.getEntityManager().find(VpnVO.class, vpnUuid);
        vpnVO.setVpnCertUuid(null);
        dbf.getEntityManager().merge(vpnVO);

        VpnCertVO vpnCert = dbf.getEntityManager().find(VpnCertVO.class, vpnCertUuid);
        vpnCert.setVpnNum(vpnCert.getVpnNum() - 1);
        dbf.getEntityManager().merge(vpnCert);
    }

    @Transactional
    private void attachVpnCert(String vpnUuid, String vpnCertUuid) {
        VpnVO vpnVO = dbf.getEntityManager().find(VpnVO.class, vpnUuid);
        vpnVO.setVpnCertUuid(vpnCertUuid);
        dbf.getEntityManager().merge(vpnVO);

        VpnCertVO vpnCert = dbf.getEntityManager().find(VpnCertVO.class, vpnCertUuid);
        vpnCert.setVpnNum(vpnCert.getVpnNum() + 1);
        dbf.getEntityManager().merge(vpnCert);
        LOGGER.debug(String.format("VPN[uuid:%s]绑定证书[uuid:%s]成功", vpnUuid, vpnCertUuid));
    }

    private void handle(APIAttachVpnCertMsg msg) {
        APIAttachVpnCertEvent evt = new APIAttachVpnCertEvent(msg.getId());

        attachVpnCert(msg.getUuid(), msg.getVpnCertUuid());

        PushCertMsg pushCertMsg = new PushCertMsg();
        pushCertMsg.setVpnUuid(msg.getUuid());

        bus.makeLocalServiceId(pushCertMsg, VpnConstant.SERVICE_ID);
        bus.send(pushCertMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
                    changeVpnState(vpn, VpnState.Enabled);
                    evt.setInventory(VpnInventory.valueOf(dbf.reload(vpn)));
                    bus.publish(evt);
                } else {
                    LOGGER.info("上传证书失败!");
                    evt.setError(reply.getError());
                    bus.publish(evt);
                }
            }
        });
    }


    private void handleLocalMessage(Message msg) {
        if (msg instanceof CheckVpnStatusMsg) {
            handle((CheckVpnStatusMsg) msg);
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

        VpnStatusCmd cmd = new VpnStatusCmd();
        cmd.vpnuuids = msg.getVpnUuids();

        String baseUrl = URLBuilder.buildHttpUrl(host.getHostIp(), VpnGlobalProperty.AGENT_PORT, VpnConstant.CHECK_VPN_STATUS_PATH);

        sendCommand(baseUrl, cmd, VpnStatusRsp.class, new ReturnValueCompletion<VpnStatusRsp>(reply) {

            @Override
            public void success(VpnStatusRsp ret) {
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

    @Override
    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.SERVICE_ID);
    }

    private <T extends AgentResponse> void sendCommand(String url, final AgentCommand cmd, final Class<T> retClass, final ReturnValueCompletion<T> completion) {

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

    private void placeCreateCert() {
        File createCert = PathUtil.findFileOnClassPath("tools/create_cert.py");
        if (createCert == null) {
            throw new CloudRuntimeException(String.format("cannot find tools/create_cert.py on classpath"));
        }

        ShellUtils.run(String.format("yes | cp %s %s", createCert.getAbsolutePath(), AnsibleConstant.ROOT_DIR));
    }

    @Override
    public boolean start() {
        placeCreateCert();
        restf.registerSyncHttpCallHandler("billing", OrderCallbackCmd.class,
                cmd -> {
                    Message message = RESTApiDecoder.loads(cmd.getCallBackData());
                    if (message instanceof CreateVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof RenewAutoVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof RenewVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof SlaVpnCallBack) {
                        updateVpnFromOrder(cmd);
                    } else if (message instanceof UnsubcribeVpnCallBack) {
                        VpnVO vpn = dbf.findByUuid(cmd.getPorductUuid(), VpnVO.class);
                        if (vpn != null) {
                            deleteVpn(vpn.getUuid(), new Completion(message) {
                                @Override
                                public void success() {
                                    dbf.removeByPrimaryKey(vpn.getUuid(), VpnVO.class);
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                    LOGGER.debug(String.format("delete vpn failed, cause by: %s", errorCode.getDetails()));
                                }
                            });
                        }
                    } else if (message instanceof UpdateVpnBandwidthCallBack) {
                        VpnVO vpn = updateVpnFromOrder(cmd);
                        if (vpn != null && vpn.getStatus() == VpnStatus.Disconnected) {
                            reconnectVpn(vpn, new Completion(null) {
                                @Override
                                public void success() {
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                }
                            });
                        }
                    } else {
                        LOGGER.debug("未知回调！！！！！！！！");
                    }
                    return null;
                });
        return true;
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

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnMsg) {
            validate((APICreateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            validate((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBandwidthMsg) {
            validate((APIUpdateVpnBandwidthMsg) msg);
        } else if (msg instanceof APIGetVpnCertMsg) {
            validate((APIGetVpnCertMsg) msg);
        } else if (msg instanceof APIUpdateVpnStateMsg) {
            validate((APIUpdateVpnStateMsg) msg);
        } else if (msg instanceof APIDeleteVpnCertMsg) {
            validate((APIDeleteVpnCertMsg) msg);
        } else if (msg instanceof APIAttachVpnCertMsg) {
            validate((APIAttachVpnCertMsg) msg);
        } else if (msg instanceof APIDetachVpnCertMsg) {
            validate((APIDetachVpnCertMsg) msg);
        } else if (msg instanceof APICreateVpnCertMsg) {
            validate((APICreateVpnCertMsg) msg);
        }
        return msg;
    }

    private void validate(APIDetachVpnCertMsg msg) {
        checkTunnelAndVpnCert(msg.getUuid());
    }

    private void validate(APIAttachVpnCertMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        checkTunnel(msg.getUuid());
        if (vpn.getVpnCertUuid() != null) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    String.format("VPN[uuid: %s]已绑定证书, 请先解绑证书", vpn.getUuid())));
        }
    }

    private void validate(APIUpdateVpnStateMsg msg) {
        checkTunnelAndVpnCert(msg.getUuid());
    }

    private void validate(APIDeleteVpnCertMsg msg) {
        VpnCertVO cert = dbf.findByUuid(msg.getUuid(), VpnCertVO.class);
        if (cert.getVpnNum() > 0) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    String.format("证书[uuid:%s]已经绑定VPN.", msg.getUuid())));
        }
    }

    private void validate(APICreateVpnCertMsg msg) {
        // 区分管理员账户
        if (msg.getSession().isAdminSession() && StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("管理员账户[uuid:%s]不能给自己创建证书",
                            msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIGetVpnCertMsg msg) {

        VpnVO vpn = Q.New(VpnVO.class).eq(VpnVO_.uuid, msg.getUuid()).find();

        String md5 = DigestUtils.md5Hex(msg.getUuid() + msg.getTimestamp() + vpn.getCertKey());
        LOGGER.debug(String.format("MD5[%s] && signature[%s]", md5, msg.getSignature()));
        boolean flag = System.currentTimeMillis() - msg.getTimestamp() > CoreGlobalProperty.INNER_MESSAGE_EXPIRE * 1000;
        if (!md5.equals(msg.getSignature()) || flag) {
            throw new ApiMessageInterceptionException(errf.stringToInvalidArgumentError(
                    String.format("消息[%s]的参数不一致。", msg.getMessageName())
            ));
        }
    }

    private void checkTunnel(String tunnelUuid) {
        if ("".equals(tunnelUuid)) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, "VPN没有指定专线。"));
        }
    }

    private void checkVpnCert(String vpnCertUuid) {
        if (vpnCertUuid == null) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    "VPN[uuid: %s]的证书已经解绑, 请绑定证书"));
        }
    }

    private void checkTunnelAndVpnCert(String vpnUuid) {
        VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
        checkTunnel(vpn.getTunnelUuid());
        checkVpnCert(vpn.getVpnCertUuid());
    }

    private void validate(APIUpdateVpnBandwidthMsg msg) {
        checkTunnelAndVpnCert(msg.getUuid());

        LocalDateTime dateTime = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(ResourceMotifyRecordVO.class)
                .eq(ResourceMotifyRecordVO_.resourceUuid, msg.getUuid())
                .gte(ResourceMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime))
                .count();
        Integer maxModifies = Q.New(VpnVO.class)
                .eq(VpnVO_.uuid, msg.getUuid())
                .select(VpnVO_.maxModifies)
                .findValue();

        if (times >= maxModifies) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    String.format("VPN[uuid:%s]修改次数已达到最大次数【%s】.", msg.getUuid(), times)));
        }
    }


    private void validate(APIUpdateVpnMsg msg) {
        if (!msg.getSession().isAdminSession()) {
            msg.setMaxModifies(null);
        }

        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName()).notEq(VpnVO_.uuid, msg.getUuid());
        if (q.isExists()) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    String.format("VPN[name:%s]已经存在.", msg.getName())));
        }
    }

    private List<String> getHostUuid(String endpointUuid, Integer vlan) {
        String sql = "select vh.uuid from VpnHostVO vh, HostInterfaceVO hi where vh.uuid = hi.hostUuid " +
                "and hi.endpointUuid = :endpointUuid and vh.status = :status and " +
                ":vlan not in (select v.vlan from VpnVO v where vh.uuid = v.hostUuid) ";

        return SQL.New(sql).param("endpointUuid", endpointUuid)
                .param("status", HostStatus.Connected).param("vlan", vlan).list();
    }

    private void validate(APICreateVpnMsg msg) {
        // 区分管理员账户
        if (msg.getSession().isAdminSession() && StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(
                    argerr("管理员账户[uuid:%s]不能给自己创建VPN",
                            msg.getSession().getAccountUuid()));
        }
        Q q = Q.New(VpnVO.class).eq(VpnVO_.name, msg.getName()).eq(VpnVO_.accountUuid, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("VPN[name:%s]已经存在。", msg.getName()));
        }
        // 物理机
        List<String> hostUuids = getHostUuid(msg.getEndpointUuid(), msg.getVlan());

        if (hostUuids.isEmpty()) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR,
                    String.format("连接点[uuid:%s]下没有可用的物理机.", msg.getEndpointUuid())));
        }
        Long sum = Q.New(VpnVO.class).in(VpnVO_.hostUuid, hostUuids).eq(VpnVO_.state, VpnState.Enabled).count();
        msg.setHostUuid(hostUuids.get((int) (sum % hostUuids.size())));
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid(), msg.getEndpointUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        if (!reply.isPayable()) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.CALL_BILLING_ERROR,
                    String.format("账户[uuid:%s]余额不足.", msg.getAccountUuid())));
        }
    }

    private void reconnectVpn(VpnVO vo, final Completion complete) {
        checkState(vo);

        StartVpnMsg vmsg = new StartVpnMsg();
        vmsg.setVpnUuid(vo.getUuid());
        bus.makeLocalServiceId(vmsg, VpnConstant.SERVICE_ID);

        bus.send(vmsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    LOGGER.debug("run command[StartVpn] success!");
                    complete.success();
                } else {
                    LOGGER.debug(String.format("run command[StartVpn] fail! ERROR:[%s]", reply.getError()));
                    complete.fail(reply.getError());
                }
            }
        });
    }

    private void deleteVpn(String uuid, final Completion complete) {
        DestroyVpnMsg destroyVpnMsg = new DestroyVpnMsg();
        destroyVpnMsg.setVpnUuid(uuid);
        bus.makeLocalServiceId(destroyVpnMsg, VpnConstant.SERVICE_ID);
        bus.send(destroyVpnMsg, new CloudBusCallBack(complete) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    LOGGER.debug("run command[destroyVpn] success!");
                    complete.success();
                } else {
                    LOGGER.debug("run command[destroyVpn] failed!");
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
                LOGGER.debug(String.format("Message[%s]:交易失败", orderMsg.getClass().getSimpleName()));
                complete.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "交易失败"));
            }
        } catch (Exception e) {
            LOGGER.debug(String.format("call billing[url: %s] failed.", url));
            complete.fail(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, String.format("调用billing[url: %s]失败.", url)));
        }
    }

    private void changeVpnStateByAPI(final VpnVO vpn, VpnState next, final Completion complete) {
        if (vpn.getState() == next) {
            complete.success();
            return;
        }
        VpnMessage vpnMessage = VpnState.Enabled == next ? new StartVpnMsg() : new StopVpnMsg();
        vpnMessage.setVpnUuid(vpn.getUuid());

        bus.makeLocalServiceId(vpnMessage, VpnConstant.SERVICE_ID);
        bus.send(vpnMessage, new CloudBusCallBack(complete) {
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

    private void checkState(VpnVO vo) {
        checkHostState(vo.getVpnHost());
        checkVpnState(vo);
    }

    private void changeVpnState(final VpnVO vpn, VpnState next) {
        VpnState currentState = vpn.getState();
        VpnVO vo = dbf.reload(vpn);
        vo.setState(next);
        dbf.updateAndRefresh(vo);
        LOGGER.debug(String.format("Vpn[%s]'s state changed from %s to %s", vpn.getUuid(), currentState, vo.getState()));
    }

    private void checkHostState(VpnHostVO vo) {
        if (HostState.Disabled == vo.getState()) {
            throw new OperationFailureException(operr("物理机已禁用"));
        }
    }

    private void checkVpnState(VpnVO vo) {
        if (VpnState.Disabled == vo.getState()) {
            throw new OperationFailureException(operr("VPN[uuid:%s, name:%s]已经用,无法操作", vo.getUuid(), vo.getName(), vo.getState()));
        }
    }

    @Override
    public List<Quota> reportQuota() {

        VpnQuotaOperator vpnQuotaOperator = new VpnQuotaOperator();
        Quota vQuota = new Quota();
        vQuota.setOperator(vpnQuotaOperator);
        vQuota.addMessageNeedValidation(APICreateVpnCertMsg.class);
        vQuota.addMessageNeedValidation(APICreateVpnMsg.class);

        Quota.QuotaPair p = new Quota.QuotaPair();
        p.setName(VpnConstant.QUOTA_VPN_CERT_NUM);
        p.setValue(QuotaConstant.QUOTA_VPN_CERT_NUM);
        vQuota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(VpnConstant.QUOTA_VPN_NUM);
        p.setValue(QuotaConstant.QUOTA_VPN_NUM);
        vQuota.addPair(p);

        return list(vQuota);
    }

}
