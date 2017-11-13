package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.*;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.*;
import com.syscxp.query.QueryFacade;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO_;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;

/**
 * Create by DCY on 2017/10/26
 */
public class TunnelManagerImpl extends AbstractService implements TunnelManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(TunnelManagerImpl.class);

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
    @Autowired
    private QueryFacade qf;

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
        TunnelBase base = new TunnelBase();
        base.handleMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateInterfaceMsg) {
            handle((APICreateInterfaceMsg) msg);
        } else if (msg instanceof APIGetVlanAutoMsg) {
            handle((APIGetVlanAutoMsg) msg);
        } else if (msg instanceof APIGetInterfacePriceMsg) {
            handle((APIGetInterfacePriceMsg) msg);
        } else if (msg instanceof APIGetTunnelPriceMsg) {
            handle((APIGetTunnelPriceMsg) msg);
        } else if (msg instanceof APIGetModifyTunnelPriceDiffMsg) {
            handle((APIGetModifyTunnelPriceDiffMsg) msg);
        } else if (msg instanceof APIUpdateInterfacePortMsg) {
            handle((APIUpdateInterfacePortMsg) msg);
        } else if (msg instanceof APIGetInterfaceTypeMsg) {
            handle((APIGetInterfaceTypeMsg) msg);
        } else if (msg instanceof APICreateInterfaceManualMsg) {
            handle((APICreateInterfaceManualMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceMsg) {
            handle((APIUpdateInterfaceMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceExpireDateMsg) {
            handle((APIUpdateInterfaceExpireDateMsg) msg);
        } else if (msg instanceof APIDeleteInterfaceMsg) {
            handle((APIDeleteInterfaceMsg) msg);
        } else if (msg instanceof APICreateTunnelMsg) {
            handle((APICreateTunnelMsg) msg);
        } else if (msg instanceof APICreateTunnelManualMsg) {
            handle((APICreateTunnelManualMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMsg) {
            handle((APIUpdateTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelBandwidthMsg) {
            handle((APIUpdateTunnelBandwidthMsg) msg);
        } else if (msg instanceof APIUpdateTunnelExpireDateMsg) {
            handle((APIUpdateTunnelExpireDateMsg) msg);
        } else if (msg instanceof APIDeleteTunnelMsg) {
            handle((APIDeleteTunnelMsg) msg);
        } else if (msg instanceof APIDeleteForciblyTunnelMsg) {
            handle((APIDeleteForciblyTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelStateMsg) {
            handle((APIUpdateTunnelStateMsg) msg);
        } else if (msg instanceof APICreateQinqMsg) {
            handle((APICreateQinqMsg) msg);
        } else if (msg instanceof APIDeleteQinqMsg) {
            handle((APIDeleteQinqMsg) msg);
        } else if (msg instanceof APIQueryTunnelDetailForAlarmMsg) {
            handle((APIQueryTunnelDetailForAlarmMsg) msg);
        } else if (msg instanceof APIListSwitchPortByTypeMsg) {
            handle((APIListSwitchPortByTypeMsg) msg);
        } else if (msg instanceof APIUpdateForciblyTunnelVlanMsg) {
            handle((APIUpdateForciblyTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            handle((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIReCallControllerMsg) {
            handle((APIReCallControllerMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    private void handle(APIListSwitchPortByTypeMsg msg) {
        List<SwitchPortVO> ports = getSwitchPortByType(msg.getUuid(), msg.getType());
        APIListSwitchPortByTypeReply reply = new APIListSwitchPortByTypeReply();
        reply.setInventories(SwitchPortInventory.valueOf(ports));
        bus.reply(msg, reply);
    }

    private void handle(APIUpdateInterfacePortMsg msg) {
        InterfaceVO iface = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        List<TunnelSwitchPortVO> tunnelSwitchPorts = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.switchPortUuid, iface.getSwitchPortUuid())
                .list();
        updateInterfacePort(msg);
        if (msg.isIssue() && !tunnelSwitchPorts.isEmpty()) {
            List<TunnelVO> tunnels = Q.New(TunnelVO.class)
                    .in(TunnelVO_.uuid, CollectionUtils.transformToList(tunnelSwitchPorts, TunnelSwitchPortVO::getTunnelUuid))
                    .list();
            List<ModifyTunnelPortsMsg> msgs = new ArrayList<>();
            for (TunnelVO tunnel : tunnels) {
                TaskResourceVO taskResource = newTaskResourceVO(tunnel, TaskType.ModifyPorts);
                ModifyTunnelPortsMsg modifyMsg = new ModifyTunnelPortsMsg();
                modifyMsg.setTunnelUuid(tunnel.getUuid());
                modifyMsg.setTaskUuid(taskResource.getUuid());
                msgs.add(modifyMsg);
            }
            bus.send(msgs);
        }
        APIUpdateInterfacePortEvent evt = new APIUpdateInterfacePortEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(dbf.reload(iface)));
        bus.publish(evt);
    }

    private void handle(APIGetVlanAutoMsg msg) {
        APIGetVlanAutoReply reply = new APIGetVlanAutoReply();

        TunnelStrategy ts = new TunnelStrategy();
        Integer vlan = ts.getVlanByStrategy(msg.getInterfaceUuid());
        if (vlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }

        reply.setVlan(vlan);
        bus.reply(msg, reply);
    }

    private void handle(APIGetInterfaceTypeMsg msg) {
        APIGetInterfaceTypeReply reply = new APIGetInterfaceTypeReply();
        reply.setTypes(getPortTypeByEndpoint(msg.getUuid()));
        bus.reply(msg, reply);
    }

    private void handle(APIGetInterfacePriceMsg msg) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(getInterfacePriceUnit(msg.getPortType()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetInterfacePriceReply(reply));
    }

    private void handle(APIGetTunnelPriceMsg msg) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getNodeAUuid(),
                msg.getNodeZUuid(), msg.getInnerEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetTunnelPriceReply(reply));
    }

    private void handle(APIGetModifyTunnelPriceDiffMsg msg) {
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        String innerEndpointUuid = null;
        TunnelSwitchPortVO tunnelSwitchPortVOB = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "B")
                .find();
        if (tunnelSwitchPortVOB != null) {
            innerEndpointUuid = tunnelSwitchPortVOB.getEndpointUuid();
        }
        EndpointVO endpointVOA = dbf.findByUuid(tunnelSwitchPortVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(tunnelSwitchPortVOZ.getEndpointUuid(), EndpointVO.class);

        APIGetModifyProductPriceDiffMsg pmsg = new APIGetModifyProductPriceDiffMsg();
        pmsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), endpointVOA.getNodeUuid(),
                endpointVOZ.getNodeUuid(), innerEndpointUuid));
        pmsg.setProductUuid(msg.getUuid());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setExpiredTime(dbf.findByUuid(msg.getUuid(), TunnelVO.class).getExpireDate());

        APIGetModifyProductPriceDiffReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetModifyTunnelPriceDiffReply(reply));
    }

    private void handle(APICreateInterfaceMsg msg) {
        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());

        //保存数据，分配资源
        InterfaceVO vo = new InterfaceVO();

        //分配资源:策略分配端口
        TunnelStrategy ts = new TunnelStrategy();
        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getEndpointUuid(), msg.getPortType());
        if (switchPortUuid == null) {
            throw new ApiMessageInterceptionException(argerr("该连接点下无可用的端口"));
        }
        //保存数据
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setType(NetworkType.TRUNK);
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setDescription(msg.getDescription());
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
        vo.setExpireDate(dbf.getCurrentSqlTime());
        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg(
                getOrderMsgForInterface(vo, msg.getPortType()));
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());

        OrderInventory orderInventory = createOrder(orderMsg);

        if (orderInventory != null) {
            //付款成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //状态修改已支付，生成到期时间
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), msg.getProductChargeModel(), msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //付款失败
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(InterfaceInventory.valueOf(vo));
        }

        bus.publish(evt);
    }

    private void handle(APICreateInterfaceManualMsg msg) {
        APICreateInterfaceManualEvent evt = new APICreateInterfaceManualEvent(msg.getId());

        //保存数据
        InterfaceVO vo = new InterfaceVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());
        if (msg.getNetworkType() == null) {
            vo.setType(NetworkType.TRUNK);
        } else {
            vo.setType(msg.getNetworkType());
        }
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setDescription(msg.getDescription());
        vo.setExpireDate(dbf.getCurrentSqlTime());
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);

        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg(getOrderMsgForInterface(vo, msg.getPortType()));
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //状态修改已支付，生成到期时间
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), msg.getProductChargeModel(), msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //付款失败
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(InterfaceInventory.valueOf(vo));
        }
        bus.publish(evt);
    }

    private void handle(APIUpdateInterfaceMsg msg) {
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        boolean update = false;
        if (msg.getName() != null) {
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateInterfaceEvent evt = new APIUpdateInterfaceEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateInterfaceExpireDateMsg msg) {
        APIUpdateInterfaceExpireDateEvent evt = new APIUpdateInterfaceExpireDateEvent(msg.getId());

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        Timestamp newTime = vo.getExpireDate();
        OrderInventory orderInventory = null;
        switch (msg.getType()) {
            case RENEW://续费
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg(
                        getOrderMsgForInterface(vo, null));
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
                renewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                renewOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(renewOrderMsg);
                break;
            case SLA_COMPENSATION://赔偿
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg = new APICreateSLACompensationOrderMsg(
                        getOrderMsgForInterface(vo, null));
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(slaCompensationOrderMsg);
                break;
        }

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(msg.getProductChargeModel());
            vo.setExpireDate(getExpireDate(newTime, msg.getProductChargeModel(), msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.publish(evt);
    }

    private void handle(APIDeleteInterfaceMsg msg) {
        APIDeleteInterfaceEvent evt = new APIDeleteInterfaceEvent(msg.getId());

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        //调用退订
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg(
                getOrderMsgForInterface(vo, null));
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //删除产品
            dbf.remove(vo);
            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //退订失败
            evt.setError(errf.stringToOperationError("退订失败"));
        }

        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateTunnelMsg msg) {

        //保存数据，分配资源
        TunnelStrategy ts = new TunnelStrategy();
        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(getVsiAuto());
        vo.setMonitorCidr(null);
        vo.setName(msg.getName());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setDuration(msg.getDuration());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);
        //根据经纬度算距离
        NodeVO nvoA = dbf.findByUuid(msg.getNodeAUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(msg.getNodeZUuid(), NodeVO.class);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(), nvoA.getLatitude(), nvoZ.getLongtitude(), nvoZ.getLatitude()));

        //给A端口分配外部vlan,并创建TunnelSwitch
        Integer vlanA = ts.getVlanByStrategy(msg.getInterfaceAUuid());
        if (vlanA == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
        TunnelSwitchPortVO tsvoA = new TunnelSwitchPortVO();
        tsvoA.setUuid(Platform.getUuid());
        tsvoA.setTunnelUuid(vo.getUuid());
        tsvoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tsvoA.setEndpointUuid(msg.getEndpointAUuid());
        tsvoA.setSwitchPortUuid(interfaceVOA.getSwitchPortUuid());
        tsvoA.setType(interfaceVOA.getType());
        tsvoA.setVlan(vlanA);
        tsvoA.setSortTag("A");

        //给Z端口分配外部vlan,并创建TunnelSwitch
        Integer vlanZ = ts.getVlanByStrategy(msg.getInterfaceZUuid());
        if (vlanZ == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
        TunnelSwitchPortVO tsvoZ = new TunnelSwitchPortVO();
        tsvoZ.setUuid(Platform.getUuid());
        tsvoZ.setTunnelUuid(vo.getUuid());
        tsvoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tsvoZ.setEndpointUuid(msg.getEndpointZUuid());
        tsvoZ.setSwitchPortUuid(interfaceVOZ.getSwitchPortUuid());
        tsvoZ.setType(interfaceVOZ.getType());
        tsvoZ.setVlan(vlanZ);
        tsvoZ.setSortTag("Z");

        //如果跨国,将出海口设备添加至TunnelSwitchPort
        if (msg.getInnerConnectedEndpointUuid() != null) {
            createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo);
        }

        dbf.persistAndRefresh(tsvoA);
        dbf.persistAndRefresh(tsvoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnel(msg.getId(),
                msg.getBandwidthOfferingUuid(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid(),
                vo,
                msg.getNodeAUuid(),
                msg.getNodeZUuid(),
                msg.getInnerConnectedEndpointUuid());
    }

    @Transactional
    private void handle(APICreateTunnelManualMsg msg) {

        //保存数据
        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(getVsiAuto());
        vo.setMonitorCidr(null);
        vo.setName(msg.getName());
        vo.setDuration(msg.getDuration());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);
        //根据经纬度算距离
        NodeVO nvoA = dbf.findByUuid(msg.getNodeAUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(msg.getNodeZUuid(), NodeVO.class);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(), nvoA.getLatitude(), nvoZ.getLongtitude(), nvoZ.getLatitude()));

        TunnelSwitchPortVO tsvoA = new TunnelSwitchPortVO();
        tsvoA.setUuid(Platform.getUuid());
        tsvoA.setTunnelUuid(vo.getUuid());
        tsvoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tsvoA.setEndpointUuid(msg.getEndpointAUuid());
        tsvoA.setSwitchPortUuid(interfaceVOA.getSwitchPortUuid());
        tsvoA.setType(interfaceVOA.getType());
        tsvoA.setVlan(msg.getaVlan());
        tsvoA.setSortTag("A");

        TunnelSwitchPortVO tsvoZ = new TunnelSwitchPortVO();
        tsvoZ.setUuid(Platform.getUuid());
        tsvoZ.setTunnelUuid(vo.getUuid());
        tsvoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tsvoZ.setEndpointUuid(msg.getEndpointZUuid());
        tsvoZ.setSwitchPortUuid(interfaceVOZ.getSwitchPortUuid());
        tsvoZ.setType(interfaceVOZ.getType());
        tsvoZ.setVlan(msg.getzVlan());
        tsvoZ.setSortTag("Z");

        //如果跨国,将出海口设备添加至TunnelSwitchPort
        if (msg.getInnerConnectedEndpointUuid() != null) {
            createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo);
        }

        //如果开启Qinq,需要指定内部vlan段
        if (interfaceVOA.getType() == NetworkType.QINQ || interfaceVOZ.getType() == NetworkType.QINQ) {
            List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
            for (InnerVlanSegment vlanSegment : vlanSegments) {
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(vo.getUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }

        dbf.persistAndRefresh(tsvoA);
        dbf.persistAndRefresh(tsvoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnel(msg.getId(),
                msg.getBandwidthOfferingUuid(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid(),
                vo,
                msg.getNodeAUuid(),
                msg.getNodeZUuid(),
                msg.getInnerConnectedEndpointUuid());
    }

    private void handle(APIUpdateTunnelVlanMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean updateA = false;
        boolean updateZ = false;
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid())) {
            tunnelSwitchPortA.setSwitchPortUuid(dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getSwitchPortUuid());
            tunnelSwitchPortA.setVlan(msg.getaVlan());
            updateA = true;
        } else {
            if (!Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
                tunnelSwitchPortA.setVlan(msg.getaVlan());
                updateA = true;
            }
        }
        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid())) {
            tunnelSwitchPortZ.setSwitchPortUuid(dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getSwitchPortUuid());
            tunnelSwitchPortZ.setVlan(msg.getzVlan());
            updateZ = true;
        } else {
            if (!Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                tunnelSwitchPortZ.setVlan(msg.getzVlan());
                updateZ = true;
            }
        }

        if (updateA)
            dbf.updateAndRefresh(tunnelSwitchPortA);
        if (updateZ)
            dbf.updateAndRefresh(tunnelSwitchPortZ);
        if (updateA || updateZ) {
            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyPorts);

            ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
            modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
            modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeTargetServiceIdByResourceUuid(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
            bus.send(modifyTunnelPortsMsg);
        }

        APIUpdateTunnelVlanEvent evt = new APIUpdateTunnelVlanEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APIUpdateForciblyTunnelVlanMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean updateA = false;
        boolean updateZ = false;
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid())) {
            tunnelSwitchPortA.setSwitchPortUuid(dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getSwitchPortUuid());
            tunnelSwitchPortA.setVlan(msg.getaVlan());
            updateA = true;
        } else {
            if (!Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
                tunnelSwitchPortA.setVlan(msg.getaVlan());
                updateA = true;
            }
        }
        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid())) {
            tunnelSwitchPortZ.setSwitchPortUuid(dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getSwitchPortUuid());
            tunnelSwitchPortZ.setVlan(msg.getzVlan());
            updateZ = true;
        } else {
            if (!Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                tunnelSwitchPortZ.setVlan(msg.getzVlan());
                updateZ = true;
            }
        }
        if (updateA)
            dbf.updateAndRefresh(tunnelSwitchPortA);
        if (updateZ)
            dbf.updateAndRefresh(tunnelSwitchPortZ);

        APIUpdateForciblyTunnelVlanEvent evt = new APIUpdateForciblyTunnelVlanEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APIUpdateTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean update = false;


        if (msg.getName() != null) {
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getDistance() != null) {
            vo.setDistance(msg.getDistance());
            update = true;
        }
        if (msg.getState() != null) {
            vo.setState(msg.getState());
            update = true;
        }
        if (msg.getStatus() != null) {
            vo.setStatus(msg.getStatus());
            update = true;
        }
        if (msg.getMonitorState() != null) {
            vo.setMonitorState(msg.getMonitorState());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateTunnelEvent evt = new APIUpdateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APIUpdateTunnelBandwidthMsg msg) {
        APIUpdateTunnelBandwidthEvent evt = new APIUpdateTunnelBandwidthEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        String nodeAUuid = getNodeUuid(vo, "A");
        String nodeZUuid = getNodeUuid(vo, "Z");

        String innerEndpointUuid = null;
        TunnelSwitchPortVO tunnelSwitchPort = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "B")
                .find();
        if (tunnelSwitchPort != null) {
            innerEndpointUuid = tunnelSwitchPort.getEndpointUuid();
        }

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        //调整次数记录表
        TunnelMotifyRecordVO record = new TunnelMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setTunnelUuid(vo.getUuid());
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setMotifyType(bandwidthOfferingVO.getBandwidth() > vo.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DEMOTION);
        dbf.persistAndRefresh(record);

        //调用支付-调整带宽
        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductName(vo.getName());
        orderMsg.setDescriptionData("no description");
        orderMsg.setCallBackData(bandwidthOfferingVO.getBandwidth().toString());
        orderMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), nodeAUuid,
                nodeZUuid, innerEndpointUuid));
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
            vo = dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyBandwidth);

            ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
            modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
            modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeTargetServiceIdByResourceUuid(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
            bus.send(modifyTunnelBandwidthMsg);
            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.publish(evt);
    }

    @Transactional
    private void handle(APIUpdateTunnelExpireDateMsg msg) {
        APIUpdateTunnelExpireDateReply reply = new APIUpdateTunnelExpireDateReply();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        Timestamp newTime = vo.getExpireDate();
        OrderInventory orderInventory = null;
        switch (msg.getType()) {
            case RENEW://续费
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
                renewOrderMsg.setProductUuid(vo.getUuid());
                renewOrderMsg.setProductName(vo.getName());
                renewOrderMsg.setProductType(ProductType.TUNNEL);
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setDescriptionData("no description");
                renewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
                renewOrderMsg.setAccountUuid(msg.getAccountUuid());
                renewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                renewOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(renewOrderMsg);
                break;
            case SLA_COMPENSATION://赔偿
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                        new APICreateSLACompensationOrderMsg();
                slaCompensationOrderMsg.setProductUuid(vo.getUuid());
                slaCompensationOrderMsg.setProductName(vo.getName());
                slaCompensationOrderMsg.setDescriptionData("no description");
                slaCompensationOrderMsg.setProductType(ProductType.TUNNEL);
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(slaCompensationOrderMsg);
                break;
        }

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(msg.getProductChargeModel());
            vo.setExpireDate(getExpireDate(newTime, msg.getProductChargeModel(), msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(TunnelInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg,reply);
    }

    @Transactional
    private void handle(APIDeleteTunnelMsg msg) {
        APIDeleteTunnelEvent evt = new APIDeleteTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //调用退订
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductName(vo.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        if (vo.getExpireDate() == null) {
            orderMsg.setExpiredTime(dbf.getCurrentSqlTime());
            orderMsg.setCreateFailure(true);
        } else {
            orderMsg.setExpiredTime(vo.getExpireDate());
        }
        orderMsg.setDescriptionData("no description");
        orderMsg.setCallBackData("delete");

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setAccountUuid(null);
            dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Delete);

            DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
            deleteTunnelMsg.setTunnelUuid(vo.getUuid());
            deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeTargetServiceIdByResourceUuid(deleteTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
            bus.send(deleteTunnelMsg);

            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("退订失败"));
        }

        bus.publish(evt);
    }

    private void handle(APIDeleteForciblyTunnelMsg msg) {
        APIDeleteForciblyTunnelEvent evt = new APIDeleteForciblyTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //调用退订
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductName(vo.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        if (vo.getExpireDate() == null) {
            orderMsg.setExpiredTime(dbf.getCurrentSqlTime());
            orderMsg.setCreateFailure(true);
        } else {
            orderMsg.setExpiredTime(vo.getExpireDate());
        }
        orderMsg.setDescriptionData("no description");
        orderMsg.setCallBackData("forciblydelete");

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            deleteTunnel(vo);

            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("退订失败"));
        }
        bus.publish(evt);
    }

    private void handle(APIUpdateTunnelStateMsg msg) {
        APIUpdateTunnelStateEvent evt = new APIUpdateTunnelStateEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.valueOf(msg.getState().toString()));

        if (msg.getState() == TunnelState.Enabled) {
            EnabledTunnelMsg enabledTunnelMsg = new EnabledTunnelMsg();
            enabledTunnelMsg.setTunnelUuid(vo.getUuid());
            enabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeTargetServiceIdByResourceUuid(enabledTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
            bus.send(enabledTunnelMsg);
        } else {
            DisabledTunnelMsg disabledTunnelMsg = new DisabledTunnelMsg();
            disabledTunnelMsg.setTunnelUuid(vo.getUuid());
            disabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeTargetServiceIdByResourceUuid(disabledTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
            bus.send(disabledTunnelMsg);
        }

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateQinqMsg msg) {
        APICreateQinqEvent evt = new APICreateQinqEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        QinqVO qinqVO = new QinqVO();
        qinqVO.setUuid(Platform.getUuid());
        qinqVO.setTunnelUuid(msg.getUuid());
        qinqVO.setStartVlan(msg.getStartVlan());
        qinqVO.setEndVlan(msg.getEndVlan());
        dbf.persistAndRefresh(qinqVO);

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyPorts);

        ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
        modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
        modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeTargetServiceIdByResourceUuid(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
        bus.send(modifyTunnelPortsMsg);

        evt.setInventory(QinqInventory.valueOf(qinqVO));
        bus.publish(evt);
    }

    private void handle(APIDeleteQinqMsg msg) {
        APIDeleteQinqEvent evt = new APIDeleteQinqEvent(msg.getId());
        QinqVO qinqVO = dbf.findByUuid(msg.getUuid(), QinqVO.class);
        TunnelVO vo = dbf.findByUuid(qinqVO.getTunnelUuid(), TunnelVO.class);
        dbf.remove(qinqVO);

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyPorts);

        ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
        modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
        modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeTargetServiceIdByResourceUuid(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
        bus.send(modifyTunnelPortsMsg);

        evt.setInventory(QinqInventory.valueOf(qinqVO));
        bus.publish(evt);
    }

    private void handle(APIQueryTunnelDetailForAlarmMsg msg) {
        Map<String, Object> detailMap = new HashMap<>();

        FalconApiCommands.Tunnel tunnelCmd = new FalconApiCommands.Tunnel();
        for (String tunnelUuid : msg.getTunnelUuidList()) {
            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).findValue();
            tunnelCmd.setTunnel_id(tunnel.getUuid());
            tunnelCmd.setBandwidth(tunnel.getBandwidth());

            List<TunnelSwitchPortVO> tunnelSwitchPortVOS = Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.tunnelUuid, tunnelUuid).list();
            for (TunnelSwitchPortVO vo : tunnelSwitchPortVOS) {
                if ("A".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointA_ip(getPhysicalSwitch(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointA_vid(vo.getVlan());
                } else if ("Z".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointB_ip(getPhysicalSwitch(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointB_vid(vo.getVlan());
                }
            }

            detailMap.put(tunnelUuid, tunnelCmd);
        }

        APIQueryTunnelDetailForAlarmReply reply = new APIQueryTunnelDetailForAlarmReply();
        reply.setMap(detailMap);
        bus.reply(msg, reply);
    }

    private void handle(APIReCallControllerMsg msg) {
        APIReCallControllerEvent evt = new APIReCallControllerEvent(msg.getId());

        TaskResourceVO vo = dbf.findByUuid(msg.getUuid(),TaskResourceVO.class);

        ReCallControllerMsg reCallControllerMsg = new ReCallControllerMsg();
        reCallControllerMsg.setTunnelUuid(vo.getResourceUuid());
        reCallControllerMsg.setTaskUuid(msg.getUuid());
        bus.makeLocalServiceId(reCallControllerMsg, TunnelConstant.SERVICE_ID);
        bus.send(reCallControllerMsg);

        evt.setInventory(TaskResourceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private boolean orderIsExist(String orderUuid) {
        return Q.New(ResourceOrderEffectiveVO.class)
                .eq(ResourceOrderEffectiveVO_.orderUuid, orderUuid)
                .isExists();
    }

    private void updateTunnelFromOrderRenewOrSla(OrderCallbackCmd cmd) {
        if (cmd.getProductType() == ProductType.PORT) {
            InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
            vo.setExpireDate(getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));

            vo.setDuration(cmd.getDuration());
            vo.setProductChargeModel(cmd.getProductChargeModel());
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        } else if (cmd.getProductType() == ProductType.TUNNEL) {
            TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
            vo.setExpireDate(getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));
            vo.setDuration(cmd.getDuration());
            vo.setProductChargeModel(cmd.getProductChargeModel());
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        }
    }

    private void updateTunnelFromOrderBuy(OrderCallbackCmd cmd) {
        if (cmd.getProductType() == ProductType.PORT) {
            InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        } else if (cmd.getProductType() == ProductType.TUNNEL) {
            TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(TunnelState.Deploying);
            vo.setStatus(TunnelStatus.Connecting);
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Create);

            CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
            createTunnelMsg.setTunnelUuid(vo.getUuid());
            createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeTargetServiceIdByResourceUuid(createTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
            bus.send(createTunnelMsg);
        }
    }

    private void updateTunnelFromOrderModifyBandwidth(OrderCallbackCmd cmd) {
        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        vo.setBandwidth(Long.valueOf(cmd.getCallBackData()));
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyBandwidth);

        ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
        modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
        modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeTargetServiceIdByResourceUuid(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
        bus.send(modifyTunnelBandwidthMsg);
    }

    @Override
    public boolean start() {
        startCleanExpiredProduct();
        restf.registerSyncHttpCallHandler(OrderType.BUY.toString(), OrderCallbackCmd.class,
                cmd -> {
                    logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                    if (!orderIsExist(cmd.getOrderUuid())) {
                        updateTunnelFromOrderBuy(cmd);
                    }

                    return null;
                });
        restf.registerSyncHttpCallHandler(OrderType.UN_SUBCRIBE.toString(), OrderCallbackCmd.class,
                cmd -> {
                    logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                    if (cmd.getProductType() == ProductType.PORT) {
                        InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
                        if (vo != null) {
                            dbf.remove(vo);
                        }
                    } else if (cmd.getProductType() == ProductType.TUNNEL) {
                        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
                        if (vo != null && vo.getAccountUuid() != null && cmd.getCallBackData().equals("delete")) {
                            vo.setAccountUuid(null);
                            dbf.updateAndRefresh(vo);

                            //创建任务
                            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Delete);

                            DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
                            deleteTunnelMsg.setTunnelUuid(vo.getUuid());
                            deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                            bus.makeTargetServiceIdByResourceUuid(deleteTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
                            bus.send(deleteTunnelMsg);
                        } else if (vo != null && vo.getAccountUuid() != null && cmd.getCallBackData().equals("forciblydelete")) {
                            deleteTunnel(vo);
                        }
                    }
                    return null;
                });

        restf.registerSyncHttpCallHandler(OrderType.RENEW.toString(), OrderCallbackCmd.class,
                cmd -> {
                    logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                    if (!orderIsExist(cmd.getOrderUuid())) {
                        updateTunnelFromOrderRenewOrSla(cmd);
                    }

                    return null;
                });
        restf.registerSyncHttpCallHandler(OrderType.SLA_COMPENSATION.toString(), OrderCallbackCmd.class,
                cmd -> {
                    logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                    if (!orderIsExist(cmd.getOrderUuid())) {
                        updateTunnelFromOrderRenewOrSla(cmd);
                    }

                    return null;
                });
        restf.registerSyncHttpCallHandler(OrderType.UPGRADE.toString(), OrderCallbackCmd.class,
                cmd -> {
                    logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                    if (!orderIsExist(cmd.getOrderUuid())) {
                        updateTunnelFromOrderModifyBandwidth(cmd);
                    }

                    return null;
                });
        restf.registerSyncHttpCallHandler(OrderType.DOWNGRADE.toString(), OrderCallbackCmd.class,
                cmd -> {
                    logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                    if (!orderIsExist(cmd.getOrderUuid())) {
                        updateTunnelFromOrderModifyBandwidth(cmd);
                    }

                    return null;
                });
        return true;
    }

    private Future<Void> cleanExpiredProductThread = null;
    private int cleanExpiredProductInterval;

    private void startCleanExpiredProduct() {
        cleanExpiredProductInterval = CoreGlobalProperty.CLEAN_EXPIRED_PRODUCT_INTERVAL;
        if (cleanExpiredProductThread != null) {
            cleanExpiredProductThread.cancel(true);
        }

        cleanExpiredProductThread = thdf.submitPeriodicTask(new CleanExpiredProductThread(), TimeUnit.SECONDS.toMillis(10));
        logger.debug(String
                .format("security group cleanExpiredProductThread starts[cleanExpiredProductInterval: %s day]", cleanExpiredProductInterval));
    }

    private class CleanExpiredProductThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.DAYS.toMillis(cleanExpiredProductInterval);
        }

        @Override
        public String getName() {
            return "clean-expired-product-" + Platform.getManagementServerId();
        }

        private List<TunnelVO> getTunnels() {

            return Q.New(TunnelVO.class)
                    .lte(TunnelVO_.expireDate, Timestamp.valueOf(LocalDateTime.now().minusDays(CoreGlobalProperty.PRODUCT_EXPIRE_DAYS)))
                    .list();
        }

        @Override
        public void run() {
            try {
                List<TunnelVO> tunnelVOs = getTunnels();
                logger.debug("delete expired tunnel.");
                if (tunnelVOs.isEmpty())
                    return;
                List<DeleteTunnelMsg> msgs = new ArrayList<>();
                for (TunnelVO vo : tunnelVOs) {
                    if (vo.getState() == TunnelState.Unpaid) {
                        deleteTunnel(vo);
                    } else {
                        vo.setAccountUuid(null);
                        dbf.updateAndRefresh(vo);
                        TaskResourceVO task = newTaskResourceVO(vo, TaskType.Delete);
                        DeleteTunnelMsg msg = new DeleteTunnelMsg();
                        msg.setTaskUuid(task.getUuid());
                        msg.setTunnelUuid(vo.getUuid());
                        bus.makeTargetServiceIdByResourceUuid(msg, TunnelConstant.SERVICE_ID, vo.getUuid());
                        msgs.add(msg);
                    }
                }
                if (msgs.isEmpty()) {
                    return;
                }
                bus.send(msgs);
            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
            }
        }
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(TunnelConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateInterfaceMsg) {
            validate((APICreateInterfaceMsg) msg);
        } else if (msg instanceof APICreateInterfaceManualMsg) {
            validate((APICreateInterfaceManualMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceMsg) {
            validate((APIUpdateInterfaceMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceExpireDateMsg) {
            validate((APIUpdateInterfaceExpireDateMsg) msg);
        } else if (msg instanceof APIDeleteInterfaceMsg) {
            validate((APIDeleteInterfaceMsg) msg);
        } else if (msg instanceof APICreateTunnelMsg) {
            validate((APICreateTunnelMsg) msg);
        } else if (msg instanceof APICreateTunnelManualMsg) {
            validate((APICreateTunnelManualMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMsg) {
            validate((APIUpdateTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelBandwidthMsg) {
            validate((APIUpdateTunnelBandwidthMsg) msg);
        } else if (msg instanceof APIUpdateTunnelExpireDateMsg) {
            validate((APIUpdateTunnelExpireDateMsg) msg);
        } else if (msg instanceof APIDeleteTunnelMsg) {
            validate((APIDeleteTunnelMsg) msg);
        } else if (msg instanceof APIDeleteForciblyTunnelMsg) {
            validate((APIDeleteForciblyTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelStateMsg) {
            validate((APIUpdateTunnelStateMsg) msg);
        } else if (msg instanceof APICreateQinqMsg) {
            validate((APICreateQinqMsg) msg);
        } else if (msg instanceof APIDeleteQinqMsg) {
            validate((APIDeleteQinqMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            validate((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateForciblyTunnelVlanMsg) {
            validate((APIUpdateForciblyTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateInterfacePortMsg) {
            validate((APIUpdateInterfacePortMsg) msg);
        }
        return msg;
    }

    private void validate(APIUpdateInterfacePortMsg msg) {

        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();
        if (iface == null)
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] does not exsit！", msg.getUuid()));
        SwitchPortVO switchPort = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, iface.getSwitchPortUuid()).find();
        if (switchPort.getPortType() == SwitchPortType.SHARE)
            throw new ApiMessageInterceptionException(
                    argerr("The type of Interface[uuid:%s] is %s, could not modify！", msg.getUuid(), switchPort.getPortType()));

        Q q = Q.New(InterfaceVO.class).eq(InterfaceVO_.switchPortUuid, msg.getSwitchPortUuid());
        if (q.isExists())
            throw new ApiMessageInterceptionException(
                    argerr("The SwitchPort[uuid:%s] has been used！", msg.getSwitchPortUuid()));
    }

    private void validate(APICreateInterfaceMsg msg) {
        //判断同一个用户的接口名称是否已经存在
        Q q1 = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.name, msg.getName())
                .eq(InterfaceVO_.accountUuid, msg.getAccountUuid());
        if (q1.isExists()) {
            throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
        }

        //类型是否支持
        List<SwitchPortType> types = getPortTypeByEndpoint(msg.getEndpointUuid());
        if (msg.getPortType() != SwitchPortType.SHARE) {
            if (!types.contains(msg.getPortType()))
                throw new ApiMessageInterceptionException(
                        argerr("该连接点[uuid:%s]下的端口[type:%s]已用完！", msg.getEndpointUuid(), msg.getPortType()));
        } else {
            Q q2 = Q.New(InterfaceVO.class)
                    .eq(InterfaceVO_.accountUuid, msg.getAccountUuid())
                    .eq(InterfaceVO_.endpointUuid, msg.getEndpointUuid());
            if (q2.isExists())
                throw new ApiMessageInterceptionException(
                        argerr("同一用户在同一连接点下只能购买一个共享端口！", msg.getEndpointUuid()));
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getPortType()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("账户[uuid:%s]余额不足!", msg.getAccountUuid()));
    }

    private void validate(APICreateInterfaceManualMsg msg) {
        //判断同一个用户的接口名称是否已经存在
        Q q = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.name, msg.getName())
                .eq(InterfaceVO_.accountUuid, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getPortType()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

    }

    private void validate(APIUpdateInterfaceMsg msg) {
        //判断同一个用户的网络名称是否已经存在
        if (!StringUtils.isEmpty(msg.getName())) {
            String accountUuid = Q.New(InterfaceVO.class)
                    .eq(InterfaceVO_.uuid, msg.getUuid())
                    .select(InterfaceVO_.accountUuid).findValue();
            if (checkResourceName(InterfaceVO.class.getSimpleName(), msg.getName(), accountUuid)) {
                throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
            }
        }

    }

    private void validate(APIUpdateInterfaceExpireDateMsg msg) {
        String accountUuid = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid, msg.getUuid())
                .select(InterfaceVO_.accountUuid).findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    private void validate(APIDeleteInterfaceMsg msg) {
        //判断云专线下是否有该物理接口
        boolean exists = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid,msg.getUuid())
                .isExists();
        if (exists) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,interface is being used!"));
        }

        //判断该产品是否有未完成订单
        String accountUuid = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid, msg.getUuid())
                .select(InterfaceVO_.accountUuid).findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    private void validate(APICreateTunnelMsg msg) {
        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }
        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(msg.getEndpointAUuid(), msg.getEndpointZUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }
        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getNodeAUuid(),
                msg.getNodeZUuid(), msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

    }

    private void validate(APICreateTunnelManualMsg msg) {

        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }

        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(msg.getEndpointAUuid(), msg.getEndpointZUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }

        //判断外部VLAN是否可用
        validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());

        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        //如果是ACCESS或是QINQ的物理接口，判断该物理接口是否已经开通通道
        if(interfaceVOA.getType() == NetworkType.ACCESS || interfaceVOA.getType() == NetworkType.QINQ){
            boolean exists = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid,msg.getInterfaceAUuid())
                    .isExists();
            if(exists){
                throw new ApiMessageInterceptionException(argerr("该物理接口不可复用"));
            }
        }
        if(interfaceVOZ.getType() == NetworkType.ACCESS || interfaceVOZ.getType() == NetworkType.QINQ){
            boolean exists = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid,msg.getInterfaceZUuid())
                    .isExists();
            if(exists){
                throw new ApiMessageInterceptionException(argerr("该物理接口不可复用"));
            }
        }

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if (msg.getVlanSegment() != null) {
            if (interfaceVOA.getType() == NetworkType.QINQ) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", interfaceVOA.getSwitchPortUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
            if (interfaceVOZ.getType() == NetworkType.QINQ) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", interfaceVOZ.getSwitchPortUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getNodeAUuid(),
                msg.getNodeZUuid(), msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }

    private void validate(APIUpdateTunnelMsg msg) {
        //判断同一个用户tunnel名称是否已经存在
        if (msg.getName() != null) {
            SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
            q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(TunnelVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
            }
        }

    }

    private void validate(APIUpdateTunnelBandwidthMsg msg) {
        //判断该产品是否有未完成订单
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());

        //调整次数当月是否达到上限
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(TunnelMotifyRecordVO.class).eq(TunnelMotifyRecordVO_.tunnelUuid, msg.getUuid())
                .gte(TunnelMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime))
                .lt(TunnelMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime.plusMonths(1))).count();
        Integer maxModifies =
                Q.New(TunnelVO.class).eq(TunnelVO_.uuid, msg.getUuid()).select(TunnelVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new ApiMessageInterceptionException(
                    argerr("The Tunnel[uuid:%s] has motified %s times.", msg.getUuid(), times));
        }
    }

    private void validate(APIUpdateTunnelExpireDateMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APIDeleteTunnelMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APIDeleteForciblyTunnelMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APIUpdateTunnelStateMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        if (vo.getState() == msg.getState()) {
            throw new ApiMessageInterceptionException(argerr("该云专线[uuid:%s] 已是该状况，不可重复操作 ", msg.getUuid()));
        }
    }

    private void validate(APICreateQinqMsg msg) {
        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        if (tunnelSwitchPortVOA.getType() == NetworkType.QINQ) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("switchPortUuid", tunnelSwitchPortVOA.getSwitchPortUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }
        if (tunnelSwitchPortVOZ.getType() == NetworkType.QINQ) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("switchPortUuid", tunnelSwitchPortVOZ.getSwitchPortUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }

    }

    private void validate(APIDeleteQinqMsg msg) {
        QinqVO qinqVO = dbf.findByUuid(msg.getUuid(), QinqVO.class);
        Long count = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid, qinqVO.getTunnelUuid())
                .count();
        if (count == 1) {
            throw new ApiMessageInterceptionException(argerr("该云专线[uuid:%s] 至少要有一个内部VLAN段，不能删！ ", qinqVO.getTunnelUuid()));
        }
    }

    private void validate(APIUpdateTunnelVlanMsg msg) {


        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());
        }

    }

    private void validate(APIUpdateForciblyTunnelVlanMsg msg) {

        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());
        }
    }

    /**
     * 判断外部VLAN是否可用
     */
    private void validateVlan(String interfaceUuid, Integer vlan) {
        TunnelStrategy ts = new TunnelStrategy();
        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuid = ts.findSwitchByInterface(interfaceUuid);

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanList = ts.findSwitchVlanBySwitch(switchUuid);

        if (vlanList.isEmpty()) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下未配置VLAN，请联系系统管理员 "));
        }

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlans = ts.fingAllocateVlanBySwitch(switchUuid);

        //判断外部VLAN是否在该虚拟交换机的VLAN段中
        Boolean inner = false;
        for (SwitchVlanVO switchVlanVO : vlanList) {
            if (vlan >= switchVlanVO.getStartVlan() && vlan <= switchVlanVO.getEndVlan()) {
                inner = true;
                break;
            }
        }
        if (!inner) {
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        //判断外部vlan是否可用
        if (!allocatedVlans.isEmpty() && allocatedVlans.contains(vlan)) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", vlan));
        }
    }

    /**
     * 判断同一用户下产品名称是否重复
     */
    private boolean checkResourceName(String resourceType, String name, String accountUuid) {
        String uuid = SQL.New(String.format("SELECT r.uuid FROM %s r WHERE r.name = :name AND r.accountUuid = :accountUuid", resourceType), String.class)
                .param("accountUuid", accountUuid)
                .param("name", name).find();
        return uuid != null;
    }

    /**
     * 判断该产品是否有未完成订单
     */
    private void checkOrderNoPay(String accountUuid, String productUuid) {
        //判断该产品是否有未完成订单
        APIGetHasNotifyMsg apiGetHasNotifyMsg = new APIGetHasNotifyMsg();
        apiGetHasNotifyMsg.setAccountUuid(accountUuid);
        apiGetHasNotifyMsg.setProductUuid(productUuid);

        APIGetHasNotifyReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(apiGetHasNotifyMsg);
        if (reply.isInventory())
            throw new ApiMessageInterceptionException(
                    argerr("该订单[uuid:%s] 有未完成操作，请稍等！", productUuid));
    }

    /**
     * 自动获取 VSI
     */
    private Integer getVsiAuto() {

        GLock glock = new GLock("maxvsi", 120);
        glock.lock();

        Integer vsi;
        String sql = "select max(vo.vsi) from TunnelVO vo";
        try {
            TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
            vsi = vq.getSingleResult();
            if (vsi == null) {
                vsi = CoreGlobalProperty.START_VSI;
            } else {
                vsi = vsi + 1;
            }

        } finally {
            glock.unlock();
        }
        return vsi;
    }

    /**
     * 调用支付
     */
    private OrderInventory createOrder(APICreateOrderMsg orderMsg) {
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
//        orderMsg.setNotifyUrl(TunnelConstant.NOTIFYURL);
        try {
            APICreateOrderReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);

            if (reply.isOrderSuccess()) {
                return reply.getInventory();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建订单, %s", e.getMessage()), e);
        }
        return null;
    }

    /**
     * 获取到期时间
     */
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

    /**
     * 获取物理接口订单信息
     */
    private APICreateOrderMsg getOrderMsgForInterface(InterfaceVO vo, SwitchPortType portType) {
        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setDescriptionData("no description");
        if (portType != null)
            orderMsg.setUnits(getInterfacePriceUnit(portType));
        orderMsg.setAccountUuid(vo.getOwnerAccountUuid());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        return orderMsg;
    }

    /**
     * 获取物理接口单价
     */
    private List<ProductPriceUnit> getInterfacePriceUnit(SwitchPortType portType) {
        List<ProductPriceUnit> units = new ArrayList<>();
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setProductTypeCode(ProductType.PORT);
        unit.setCategoryCode(Category.PORT);
        unit.setAreaCode("DEFAULT");
        unit.setLineCode("DEFAULT");
        unit.setConfigCode(getPortOfferingUuid(portType));
        units.add(unit);
        return units;
    }

    /**
     * 根据端口类型获取端口规格UUID
     */
    private String getPortOfferingUuid(SwitchPortType type) {
        return Q.New(PortOfferingVO.class)
                .eq(PortOfferingVO_.type, type)
                .select(PortOfferingVO_.uuid)
                .findValue();
    }

    /**
     * 获取云专线单价
     */
    private List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid, String nodeAUuid, String nodeZUuid, String innerEndpointUuid) {
        List<ProductPriceUnit> units = new ArrayList<>();
        NodeVO nodeA = dbf.findByUuid(nodeAUuid, NodeVO.class);
        NodeVO nodeZ = dbf.findByUuid(nodeZUuid, NodeVO.class);
        String zoneUuidA = getZoneUuid(nodeA.getUuid());
        String zoneUuidZ = getZoneUuid(nodeZ.getUuid());
        if (innerEndpointUuid == null) {  //国内互传  或者 国外到国外
            if (nodeA.getCountry().equals("CHINA") && nodeZ.getCountry().equals("CHINA")) {  //国内互传
                ProductPriceUnit unit = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeA, nodeZ, zoneUuidA, zoneUuidZ);
                units.add(unit);
            } else {                          //国外到国外
                ProductPriceUnit unit = getTunnelPriceUnitAb(bandwidthOfferingUuid, nodeA, nodeZ);
                units.add(unit);
            }
        } else {                          //跨国
            EndpointVO endpointVO = dbf.findByUuid(innerEndpointUuid, EndpointVO.class);
            NodeVO nodeB = dbf.findByUuid(endpointVO.getNodeUuid(), NodeVO.class);
            String zoneUuidB = getZoneUuid(nodeB.getUuid());

            ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeA, nodeB, zoneUuidA, zoneUuidB);
            ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeZ);

            units.add(unitInner);
            units.add(unitOuter);
        }
        return units;
    }

    /**
     * 获取云专线单价--国内互传单价
     */
    private ProductPriceUnit getTunnelPriceUnitCN(String bandwidthOfferingUuid, NodeVO nodeA, NodeVO nodeZ, String zoneUuidA, String zoneUuidZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        Category category;
        String areaCode;
        String lineCode;

        if (nodeA.getCity().equals(nodeZ.getCity())) {  //同城
            category = Category.CITY;
            areaCode = "DEFAULT";
            lineCode = "DEFAULT";
        } else if (zoneUuidA != null && zoneUuidZ != null && zoneUuidA.equals(zoneUuidZ)) { //同区域
            category = Category.REGION;
            areaCode = zoneUuidA;
            lineCode = "DEFAULT";
        } else {                      //长传
            category = Category.LONG;
            areaCode = "DEFAULT";
            lineCode = "DEFAULT";
        }
        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(category);
        unit.setAreaCode(areaCode);
        unit.setLineCode(lineCode);
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 获取云专线单价--国内到国外单价
     */
    private ProductPriceUnit getTunnelPriceUnitCNToAb(String bandwidthOfferingUuid, NodeVO nodeB, NodeVO nodeZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(Category.ABROAD);
        unit.setAreaCode("CHINA2ABROAD");
        unit.setLineCode(nodeB.getCity() + "/" + nodeZ.getCountry());
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 获取云专线单价--国外到国外单价
     */
    private ProductPriceUnit getTunnelPriceUnitAb(String bandwidthOfferingUuid, NodeVO nodeA, NodeVO nodeZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(Category.ABROAD);
        unit.setAreaCode("ABROAD");
        unit.setLineCode(nodeA.getCountry() + "/" + nodeZ.getCountry());
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 根据节点找到所属区域
     */
    private String getZoneUuid(String nodeUuid) {
        String zoneUuid = null;
        ZoneNodeRefVO zoneNodeRefVO = Q.New(ZoneNodeRefVO.class)
                .eq(ZoneNodeRefVO_.nodeUuid, nodeUuid)
                .find();
        if (zoneNodeRefVO != null) {
            zoneUuid = zoneNodeRefVO.getZoneUuid();
        }
        return zoneUuid;
    }

    /**
     * 付款成功,记录生效订单
     */
    private void saveResourceOrderEffective(String orderUuid, String resourceUuid, String resourceType) {
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceUuid(resourceUuid);
        resourceOrderEffectiveVO.setResourceType(resourceType);
        resourceOrderEffectiveVO.setOrderUuid(orderUuid);
        dbf.persistAndRefresh(resourceOrderEffectiveVO);
    }

    /**
     * 创建云专线的支付和下发
     */
    private void afterCreateTunnel(String msgId,
                                   String bandwidthOfferingUuid,
                                   String accountUuid,
                                   String opAccountUuid,
                                   TunnelVO vo,
                                   String nodeAuuid,
                                   String nodeZuuid,
                                   String innerEndpointUuid) {
        APICreateTunnelEvent evt = new APICreateTunnelEvent(msgId);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setUnits(getTunnelPriceUnit(bandwidthOfferingUuid, nodeAuuid, nodeZuuid, innerEndpointUuid));
        orderMsg.setAccountUuid(accountUuid);
        orderMsg.setOpAccountUuid(opAccountUuid);
        orderMsg.setDescriptionData("no description");

        OrderInventory orderInventory = createOrder(orderMsg);

        if (orderInventory == null) {
            vo.setExpireDate(dbf.getCurrentSqlTime());
            vo = dbf.updateAndRefresh(vo);
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);
            return;
        }

        //支付成功修改状态,记录生效订单
        saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        vo = dbf.updateAndRefresh(vo);

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Create);

        CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
        createTunnelMsg.setTunnelUuid(vo.getUuid());
        createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeTargetServiceIdByResourceUuid(createTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
        bus.send(createTunnelMsg);

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    /**
     * 创建云专线 支付成功创建下发任务
     */
    private TaskResourceVO newTaskResourceVO(TunnelVO vo, TaskType taskType) {
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setResourceUuid(vo.getUuid());
        taskResourceVO.setResourceType(vo.getClass().getSimpleName());
        taskResourceVO.setTaskType(taskType);
        taskResourceVO.setBody(null);
        taskResourceVO.setResult(null);
        taskResourceVO.setStatus(TaskStatus.Preexecute);
        taskResourceVO = dbf.persistAndRefresh(taskResourceVO);
        return taskResourceVO;
    }

    /**
     * 创建云专线 如果跨国,将出海口设备添加至TunnelSwitchPort
     */
    private void createTunnelSwitchPortForAbroad(String innerConnectedEndpointUuid, TunnelVO vo) {
        TunnelStrategy ts = new TunnelStrategy();

        //通过互联连接点找到内联交换机和内联端口
        SwitchVO innerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.INNER)
                .find();
        if (innerSwitch == null) {
            throw new ApiMessageInterceptionException(argerr("该互联连接点下未添加内联逻辑交换机 "));
        }
        SwitchPortVO innerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, innerSwitch.getUuid())
                .find();
        if (innerSwitchPort == null) {
            throw new ApiMessageInterceptionException(argerr("该内联逻辑交换机下未添加端口 "));
        }
        //通过互联连接点找到外联交换机和外联端口
        SwitchVO outerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.OUTER)
                .find();
        if (outerSwitch == null) {
            throw new ApiMessageInterceptionException(argerr("该互联连接点下未添加外联逻辑交换机 "));
        }
        SwitchPortVO outerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, outerSwitch.getUuid())
                .find();
        if (outerSwitchPort == null) {
            throw new ApiMessageInterceptionException(argerr("该外联逻辑交换机下未添加端口 "));
        }
        //获取互联设备的VLAN
        Integer innerVlan = ts.getInnerVlanByStrategy(innerSwitch.getUuid());
        if (innerVlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属内联虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }

        TunnelSwitchPortVO tsvoB = new TunnelSwitchPortVO();
        tsvoB.setUuid(Platform.getUuid());
        tsvoB.setTunnelUuid(vo.getUuid());
        tsvoB.setInterfaceUuid(null);
        tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
        tsvoB.setSwitchPortUuid(innerSwitchPort.getUuid());
        tsvoB.setType(NetworkType.TRUNK);
        tsvoB.setVlan(innerVlan);
        tsvoB.setSortTag("B");

        TunnelSwitchPortVO tsvoC = new TunnelSwitchPortVO();
        tsvoC.setUuid(Platform.getUuid());
        tsvoC.setTunnelUuid(vo.getUuid());
        tsvoC.setInterfaceUuid(null);
        tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
        tsvoC.setSwitchPortUuid(outerSwitchPort.getUuid());
        tsvoC.setType(NetworkType.TRUNK);
        tsvoC.setVlan(innerVlan);
        tsvoC.setSortTag("C");

        dbf.persistAndRefresh(tsvoB);
        dbf.persistAndRefresh(tsvoC);
    }

    /**
     * 根据TunnelSwicth获取两端节点
     */
    private String getNodeUuid(TunnelVO vo, String sortTag) {
        TunnelSwitchPortVO tunnelSwitch = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, sortTag)
                .find();
        return dbf.findByUuid(tunnelSwitch.getEndpointUuid(), EndpointVO.class).getNodeUuid();
    }

    /**
     * 修改interface 的 switchPort
     */
    private void updateInterfacePort(APIUpdateInterfacePortMsg msg) {
        String switchPortUuid = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid, msg.getUuid())
                .select(InterfaceVO_.switchPortUuid)
                .find();

        UpdateQuery.New(InterfaceVO.class)
                .set(InterfaceVO_.switchPortUuid, msg.getSwitchPortUuid())
                .set(InterfaceVO_.type, msg.getNetworkType())
                .eq(InterfaceVO_.uuid, msg.getUuid())
                .update();

        UpdateQuery.New(TunnelSwitchPortVO.class)
                .set(TunnelSwitchPortVO_.switchPortUuid, msg.getSwitchPortUuid())
                .set(TunnelSwitchPortVO_.type, msg.getNetworkType())
                .eq(TunnelSwitchPortVO_.switchPortUuid, switchPortUuid)
                .update();
    }

    /**
     * 通过连接点获取可用的端口规格
     */
    private List<SwitchPortType> getPortTypeByEndpoint(String endpointUuid) {
        List<String> switchs = CollectionUtils.transformToList(getSwitchByEndpoint(endpointUuid), SwitchAO::getUuid);
        if (switchs.isEmpty())
            return Collections.emptyList();
        return Q.New(SwitchPortVO.class)
                .in(SwitchPortVO_.switchUuid, switchs)
                .eq(SwitchPortVO_.state, SwitchPortState.Enabled)
                .select(SwitchPortVO_.portType)
                .groupBy(SwitchPortVO_.portType)
                .listValues();
    }

    /**
     * 通过连接点获取可用的逻辑交换机
     */
    private List<SwitchVO> getSwitchByEndpoint(String endpointUuid) {
        return Q.New(SwitchVO.class)
                .eq(SwitchVO_.state, SwitchState.Enabled)
                .eq(SwitchVO_.status, SwitchStatus.Connected)
                .eq(SwitchVO_.endpointUuid, endpointUuid)
                .list();
    }

    /**
     * 通过连接点和端口规格获取可用的端口
     */
    private List<SwitchPortVO> getSwitchPortByType(String endpointUuid, SwitchPortType type) {
        List<String> switchs = CollectionUtils.transformToList(getSwitchByEndpoint(endpointUuid), SwitchAO::getUuid);
        if (switchs.isEmpty())
            return Collections.emptyList();
        return Q.New(SwitchPortVO.class)
                .in(SwitchPortVO_.switchUuid, switchs)
                .eq(SwitchPortVO_.state, SwitchPortState.Enabled)
                .eq(SwitchPortVO_.portType, type)
                .list();
    }

    /**
     * 通过端口获取物理交换机的管理IP
     */
    private String getPhysicalSwitch(String switchPortUuid) {
        String switcUuid = Q.New(SwitchPortVO.class).
                eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class).
                eq(SwitchVO_.uuid, switcUuid)
                .select(SwitchVO_.physicalSwitchUuid).findValue();

        String switchIp = Q.New(PhysicalSwitchVO.class).
                eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid).
                select(PhysicalSwitchVO_.mIP).findValue();

        if (switchIp.isEmpty())
            throw new IllegalArgumentException("获取物理交换机IP失败");

        return switchIp;
    }

    /**
     * 删除TUNNEL及其关联表
     */
    private void deleteTunnel(TunnelVO vo) {
        dbf.remove(vo);
        //删除对应的  TunnelSwitchPortVO 和 QingqVO
        SimpleQuery<TunnelSwitchPortVO> q = dbf.createQuery(TunnelSwitchPortVO.class);
        q.add(TunnelSwitchPortVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<TunnelSwitchPortVO> tivList = q.list();
        if (tivList.size() > 0) {
            for (TunnelSwitchPortVO tiv : tivList) {
                dbf.remove(tiv);
            }
        }

        SimpleQuery<QinqVO> q2 = dbf.createQuery(QinqVO.class);
        q2.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<QinqVO> qinqList = q2.list();
        if (qinqList.size() > 0) {
            for (QinqVO qv : qinqList) {
                dbf.remove(qv);
            }
        }
    }

}
