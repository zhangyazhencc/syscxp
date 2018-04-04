package com.syscxp.vpn.l3vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SQL;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.billing.*;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.host.HostStatus;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.vpn.agent.*;
import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.*;
import com.syscxp.header.vpn.l3vpn.APICreateL3VpnMsg;
import com.syscxp.header.vpn.l3vpn.APICreateL3VpnEvent;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.job.DeleteRenewVOAfterDeleteResourceJob;
import com.syscxp.vpn.vpn.VpnCommands;
import com.syscxp.vpn.vpn.VpnGlobalProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (msg instanceof VpnMessage) {
            System.out.println("vpnhhhhhhhhh");
            passThrough((VpnMessage) msg);
        } else if (msg instanceof APIMessage) {
            System.out.println("vpniiiiiii");
            handleApiMessage((APIMessage) msg);
        } else {
            System.out.println("vpnjjjjjjjj");
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
        L3VpnBase base = new L3VpnBase(vo);
        base.handleMessage(msg);
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
            System.out.println("vpnkkkkkkk");
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

    private void doAddVpn(final APICreateL3VpnMsg msg, ReturnValueCompletion<VpnInventory> returnValueCompletion) {
        System.out.println("eenenennenenen ");
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
        if (msg.getProductChargeModel() == null)
            msg.setProductChargeModel(ProductChargeModel.BY_MONTH);

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid(), msg.getEndpointUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        if (!reply.isPayable()) {
            throw new OperationFailureException(errf.instantiateErrorCode(VpnErrors.CALL_BILLING_ERROR,
                    String.format("账户[uuid:%s]余额不足.", msg.getAccountUuid())));
        }

    }

    private List<String> getHostUuid(String endpointUuid, Integer vlan) {
        String sql = "select vh.uuid from VpnHostVO vh, HostInterfaceVO hi where vh.uuid = hi.hostUuid " +
                "and hi.endpointUuid = :endpointUuid and vh.status = :status and " +
                ":vlan not in (select v.vlan from VpnVO v where vh.uuid = v.hostUuid) ";

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
}
