package org.zstack.vpn.vpn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.Q;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.identity.InnerMessageHelper;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.core.thread.Task;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.billing.*;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.AccountType;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTConstant;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.query.QueryOp;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.vpn.header.vpn.*;
import org.zstack.vpn.vpn.VpnCommands.*;
import org.zstack.vpn.header.host.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    private void handle(APIGetVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        VpnInventory inventory = VpnInventory.valueOf(vpn);

        APIGetVpnReply reply = new APIGetVpnReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);
    }


    @Transactional
    public void handle(APICreateVpnMsg msg) {
        final VpnVO vpn = new VpnVO();
        vpn.setUuid(Platform.getUuid());
        vpn.setAccountUuid(msg.getAccountUuid());
        vpn.setDescription(msg.getDescription());
        vpn.setName(msg.getDescription());
        vpn.setVpnCidr(msg.getVpnCidr());
        vpn.setBandwidth(msg.getBandwidth());
        vpn.setEndpointUuid(msg.getEndpointUuid());
        vpn.setState(VpnState.Creating);
        vpn.setStatus(VpnStatus.Disconnected);
        vpn.setDuration(msg.getDuration());
        vpn.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));
        vpn.setPort(generatePort(msg.getHostUuid()));
        vpn.setHostUuid(msg.getHostUuid());
        VpnHostVO vpnHost = dbf.findByUuid(msg.getHostUuid(), VpnHostVO.class);
        vpn.setVpnHost(vpnHost);

        final VpnInterfaceVO vpnIface = new VpnInterfaceVO();
        vpnIface.setUuid(Platform.getUuid());
        vpnIface.setName(msg.getName() + msg.getVlan());
        vpnIface.setVpnUuid(vpn.getUuid());
        vpnIface.setLocalIp(msg.getLocalIp());
        vpnIface.setNetmask(msg.getNetmask());
        vpnIface.setVlan(msg.getVlan());
        vpnIface.setNetworkUuid(msg.getNetworkUuid());

        List<VpnInterfaceVO> interfaces = new ArrayList<>();
        interfaces.add(vpnIface);
        vpn.setVpnInterfaces(interfaces);

        //保存vpn
        dbf.getEntityManager().persist(vpn);
        dbf.getEntityManager().persist(vpnIface);
        dbf.getEntityManager().flush();

        //Todo create order
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vpn.getName());
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        orderMsg.setDuration(vpn.getDuration());
        orderMsg.setProductDescription(vpn.getDescription());
        orderMsg.setProductPriceUnitUuids(msg.getProductPriceUnitUuids());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        createOrder(orderMsg);


        CreateVpnCmd cmd = CreateVpnCmd.valueOf(vpn);
        CreateVpnResponse response = new VpnRESTCaller()
                .syncPostForVPN(VpnConstant.CREATE_VPN_PATH, cmd, CreateVpnResponse.class);
        System.out.println(response.isSuccess());
        checkVpnCreateState(cmd);


        APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    private void createOrder(APICreateOrderMsg orderMsg) {
        InnerMessageHelper.setMD5(orderMsg);
        RestAPIResponse rsp;
        try {
            rsp = new VpnRESTCaller(VpnGlobalProperty.BILLING_SERVER_URL).syncPost(RESTConstant.REST_API_CALL, orderMsg);
        } catch (InterruptedException e) {
            throw new CloudRuntimeException(String.format("failed to post to %s, Exception: ", VpnGlobalProperty.BILLING_SERVER_URL, e.getMessage()));
        }
        APIEvent apiEvent = (APIEvent) RESTApiDecoder.loads(rsp.getResult());
        if (!apiEvent.isSuccess()) {
            throw new CloudRuntimeException(String.format("failed to create order, code: %s, detail: %s", apiEvent.getError().getCode(), apiEvent.getError().getDetails()));
        }
    }

    private void checkVpnCreateState(CreateVpnCmd cmd) {
        logger.debug("checkVpnCreateState");
        thdf.submit(new Task<Object>() {

            @Override
            public Object call() throws Exception {
                CheckStateResponse rsp = new VpnRESTCaller().checkState(VpnConstant.CHECK_CREATE_STATE_PATH, cmd);
                VpnVO vpnVO = dbf.findByUuid(cmd.getVpnUuid(), VpnVO.class);
                switch (rsp.getState()) {
                    case Success:
                        vpnVO.setState(VpnState.Enabled);
                        vpnVO.setStatus(VpnStatus.Connected);
                        break;
                    case Failure:
                        vpnVO.setState(VpnState.Enabled);
                        vpnVO.setStatus(VpnStatus.Connected);
                        break;
                    default:
                        break;
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
        Q q = Q.New(VpnVO.class)
                .eq(VpnVO_.hostUuid, hostUuid)
                .orderBy(VpnVO_.port, SimpleQuery.Od.DESC)
                .limit(1)
                .select(VpnVO_.port);
        boolean flag = q.isExists();
        if (!flag)
            return 30000;
        return (Integer) q.findValue() + 1;
    }

    @Transactional
    public void handle(APIUpdateVpnExpireDateMsg msg) {

        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        LocalDateTime newTime = vpn.getExpireDate().toLocalDateTime();
        switch (msg.getType()) {
            case RENEW:
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
                renewOrderMsg.setProductUuid(vpn.getUuid());
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setAccountUuid(msg.getAccountUuid());
                renewOrderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                createOrder(renewOrderMsg);
                newTime = newTime.plusMonths(msg.getDuration());
                break;
            case SLA_COMPENSATION:
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg = new APICreateSLACompensationOrderMsg();
                slaCompensationOrderMsg.setProductUuid(vpn.getUuid());
                slaCompensationOrderMsg.setProductName(vpn.getName());
                slaCompensationOrderMsg.setProductDescription(vpn.getDescription());
                slaCompensationOrderMsg.setProductType(ProductType.VPN);
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getOpAccountUuid());
                createOrder(slaCompensationOrderMsg);
                newTime = newTime.plusDays(msg.getDuration());
                break;
            default:
                break;
        }
        vpn.setExpireDate(Timestamp.valueOf(newTime));

        dbf.updateAndRefresh(vpn);
        APIUpdateVpnExpireDateEvent evt = new APIUpdateVpnExpireDateEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnBandwidthMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setBandwidth(msg.getBandwidth());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vpn.getUuid());
        orderMsg.setProductName(vpn.getName());
        orderMsg.setProductDescription(vpn.getDescription());
        orderMsg.setProductPriceUnitUuids(msg.getProductPriceUnitUuids());
        orderMsg.setProductType(ProductType.VPN);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getOpAccountUuid());
        createOrder(orderMsg);

        UpdateVpnBandWidthCmd cmd = UpdateVpnBandWidthCmd.valueOf(vpn);
        new VpnRESTCaller().syncPostForVPN(VpnConstant.UPDATE_VPN_BANDWIDTH_PATH, cmd, AddVpnHostResponse.class);

        vpn = dbf.persistAndRefresh(vpn);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            vpn.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            vpn.setDescription(msg.getDescription());
            update = true;
        }
        if (update)
            dbf.updateAndRefresh(vpn);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnStateMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        if (msg.getState() != vpn.getState()) {
            vpn.setState(msg.getState());

            UpdateVpnStateCmd cmd = UpdateVpnStateCmd.valueOf(vpn);
            switch (msg.getState()) {
                case Enabled:
                    new VpnRESTCaller().syncPostForVPN(VpnConstant.START_VPN_PATH, cmd, UpdateVpnStateResponse.class);
                    break;
                case Disabled:
                    new VpnRESTCaller().syncPostForVPN(VpnConstant.STOP_VPN_PATH, cmd, UpdateVpnStateResponse.class);
                    break;
                default:
                    break;
            }
            vpn = dbf.updateAndRefresh(vpn);
        }
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(vpn));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIUpdateVpnCidrMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        vpn.setVpnCidr(msg.getVpnCidr());

        // Todo update vpn cidr
        UpdateVpnCidrCmd cmd = UpdateVpnCidrCmd.valueOf(vpn);
        new VpnRESTCaller().syncPostForVPN(VpnConstant.UPDATE_VPN_CIDR_PATH, cmd, UpdateVpnCidrResponse.class);


        vpn = dbf.persistAndRefresh(vpn);
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
        createOrder(orderMsg);



        DeleteVpnCmd cmd = DeleteVpnCmd.valueOf(vpn);
        new VpnRESTCaller().syncPostForVPN(VpnConstant.UPDATE_VPN_BANDWIDTH_PATH, cmd, DeleteVpnResponse.class);


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

        //Todo create vpn iface
        VpnInterfaceCmd cmd = VpnInterfaceCmd.valueOf(msg.getLocalIP(), iface);
        new VpnRESTCaller().syncPostForVPN(VpnConstant.ADD_VPN_INTERFACE_PATH, cmd, VpnInterfaceResponse.class);


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
        new VpnRESTCaller().syncPostForVPN(VpnConstant.DELETE_VPN_INTERFACE_PATH, cmd, VpnInterfaceResponse.class);

        //Todo delete vpn iface
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

        //Todo create vpn route
        VpnVO vpn = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        VpnRouteCmd cmd = VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);
        new VpnRESTCaller().syncPostForVPN(VpnConstant.ADD_VPN_ROUTE_PATH, cmd, VpnRouteResponse.class);

        route = dbf.persistAndRefresh(route);
        APICreateVpnRouteEvent evt = new APICreateVpnRouteEvent(msg.getId());
        evt.setInventory(VpnRouteInventory.valueOf(route));
        bus.publish(evt);
    }

    @Transactional
    public void handle(APIDeleteVpnRouteMsg msg) {
        VpnRouteVO route = dbf.findByUuid(msg.getUuid(), VpnRouteVO.class);

        //Todo delete vpn route
        VpnVO vpn = dbf.findByUuid(route.getVpnUuid(), VpnVO.class);
        VpnRouteCmd cmd = VpnRouteCmd.valueOf(vpn.getVpnHost().getManageIp(), route);
        new VpnRESTCaller().syncPostForVPN(VpnConstant.ADD_VPN_ROUTE_PATH, cmd, VpnRouteResponse.class);

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

    public boolean start() {
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
        }
        return msg;
    }


    private void validate(APIUpdateVpnMsg msg) {
        Q q = Q.New(VpnVO.class)
                .eq(VpnVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The Vpn[name:%s] is already exist.", msg.getName()
            ));
    }

    private void validate(APIQueryVpnMsg msg) {
        if (msg.getSession().getType() != AccountType.SystemAdmin) {
            msg.addQueryCondition(VpnVO_.accountUuid.toString(), QueryOp.EQ, msg.getSession().getAccountUuid());
        }
    }

    private void validate(APICreateVpnMsg msg) {
        if (msg.getSession().getType() == AccountType.Normal && StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Account[uuid:%s] is not a admin or proxy.", msg.getSession().getAccountUuid()
            ));
        }
        Q q = Q.New(VpnVO.class)
                .eq(VpnVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The Vpn[name:%s] is already exist.", msg.getName()
            ));
    }

}