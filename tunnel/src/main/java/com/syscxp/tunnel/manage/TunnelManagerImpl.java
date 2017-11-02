package com.syscxp.tunnel.manage;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.header.tunnel.*;
import com.syscxp.query.QueryFacade;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.header.node.ZoneNodeRefVO;
import com.syscxp.tunnel.header.node.ZoneNodeRefVO_;
import com.syscxp.tunnel.header.node.ZoneVO;
import com.syscxp.tunnel.header.switchs.*;
import com.syscxp.tunnel.header.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
        } else if (msg instanceof APIGetInterfacePriceMsg) {
            handle((APIGetInterfacePriceMsg) msg);
        } else if (msg instanceof APIGetTunnelPriceMsg) {
            handle((APIGetTunnelPriceMsg) msg);
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
        } else if(msg instanceof APIQueryTunnelDetailForAlarmMsg){
            handle((APIQueryTunnelDetailForAlarmMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
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
                vsi = 1;
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
        //orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setNotifyUrl(TunnelConstant.NOTIFYURL);
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

    private void handle(APIGetInterfaceTypeMsg msg) {
        APIGetInterfaceTypeReply reply = new APIGetInterfaceTypeReply();
        reply.setTypes(getPortTypeByEndpoint(msg.getUuid()));
        bus.reply(msg, reply);
    }

    private List<SwitchPortType> getPortTypeByEndpoint(String endpointUuid) {
        List<String> switchs = Q.New(SwitchVO.class)
                .eq(SwitchVO_.state, SwitchState.Enabled)
                .eq(SwitchVO_.status, SwitchStatus.Connected)
                .eq(SwitchVO_.endpointUuid, endpointUuid)
                .select(SwitchVO_.uuid).listValues();
        if (switchs.isEmpty())
            return Collections.emptyList();
        return Q.New(SwitchPortVO.class)
                .in(SwitchPortVO_.switchUuid, switchs)
                .eq(SwitchPortVO_.state, SwitchPortState.Enabled)
                .select(SwitchPortVO_.portType)
                .groupBy(SwitchPortVO_.portType)
                .listValues();
    }

    private void handle(APIGetInterfacePriceMsg msg) {
        APIGetInterfacePriceReply reply = new APIGetInterfacePriceReply();

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(getInterfacePriceUnit(msg.getPortOfferingUuid()));
        reply =  new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(msg);

        bus.reply(msg, reply);
    }

    private void handle(APIGetTunnelPriceMsg msg) {
        APIGetTunnelPriceReply reply = new APIGetTunnelPriceReply();

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(getTunnelPriceUnitCopy(msg.getBandwidthOfferingUuid(),msg.getNodeAUuid(),msg.getNodeZUuid(),msg.getInnerEndpointUuid()));

        reply =  new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(msg);

        bus.reply(msg, reply);
    }

    private void handle(APICreateInterfaceMsg msg) {
        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());

        //获取端口配置
        PortOfferingVO portOffering = dbf.findByUuid(msg.getPortOfferingUuid(), PortOfferingVO.class);
        //保存数据，分配资源
        InterfaceVO vo = new InterfaceVO();

        //分配资源:策略分配端口
        TunnelStrategy ts = new TunnelStrategy();
        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getEndpointUuid(), portOffering.getType());
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
        APICreateBuyOrderMsg orderMsg = getOrderMsgForInterface(vo, msg.getPortOfferingUuid());
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
        if (msg.getType() == null) {
            vo.setType(NetworkType.TRUNK);
        } else {
            vo.setType(msg.getType());
        }
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setDescription(msg.getDescription());
        vo.setExpireDate(dbf.getCurrentSqlTime());
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);

        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = getOrderMsgForInterface(vo, msg.getPortOfferingUuid());
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

    private <T extends APICreateOrderMsg> T getOrderMsgForInterface(InterfaceVO vo, String portOfferingUuid) {
        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setDescriptionData("no description");
        if (portOfferingUuid != null)
            orderMsg.setUnits(getInterfacePriceUnit(portOfferingUuid));
        orderMsg.setAccountUuid(vo.getOwnerAccountUuid());
        orderMsg.setNotifyUrl(TunnelConstant.NOTIFYURL);
        return (T) orderMsg;
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
                APICreateRenewOrderMsg renewOrderMsg = getOrderMsgForInterface(vo, null);
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
                renewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                renewOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(renewOrderMsg);
                break;
            case SLA_COMPENSATION://赔偿
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg = getOrderMsgForInterface(vo, null);
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
        APICreateUnsubcribeOrderMsg orderMsg = getOrderMsgForInterface(vo, null);
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

    private void afterCreateTunnel(String msgId,String bandwidthOfferingUuid,String accountUuid,String opAccountUuid,TunnelVO vo) {
        APICreateTunnelEvent evt = new APICreateTunnelEvent(msgId);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setUnits(getTunnelPriceUnit(bandwidthOfferingUuid));
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

    @Transactional
    private void handle(APICreateTunnelMsg msg) {

        //保存数据，分配资源
        TunnelStrategy ts = new TunnelStrategy();
        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        vo.setUuid(Platform.getUuid());

        //给A端口分配外部vlan,并创建TunnelInterface
        Integer vlanA = ts.getInnerVlanByStrategy(msg.getInterfaceAUuid());
        if (vlanA == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
        TunnelInterfaceVO tivoA = new TunnelInterfaceVO();
        tivoA.setUuid(Platform.getUuid());
        tivoA.setTunnelUuid(vo.getUuid());
        tivoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tivoA.setVlan(vlanA);
        tivoA.setSortTag("A");
        tivoA.setQinqState(TunnelQinqState.Disabled);
        tivoA.setInterfaceVO(dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class));


        //给Z端口分配外部vlan,并创建TunnelInterface
        Integer vlanZ = ts.getInnerVlanByStrategy(msg.getInterfaceZUuid());
        if (vlanZ == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
        TunnelInterfaceVO tivoZ = new TunnelInterfaceVO();
        tivoZ.setUuid(Platform.getUuid());
        tivoZ.setTunnelUuid(vo.getUuid());
        tivoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tivoZ.setVlan(vlanZ);
        tivoZ.setSortTag("Z");
        tivoZ.setQinqState(TunnelQinqState.Disabled);
        tivoZ.setInterfaceVO(dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class));


        //根据经纬度算距离
        NodeVO nvoA = dbf.findByUuid(msg.getNodeAUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(msg.getNodeZUuid(), NodeVO.class);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(), nvoA.getLatitude(), nvoZ.getLongtitude(), nvoZ.getLatitude()));

        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(getVsiAuto());
        vo.setName(msg.getName());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setDuration(msg.getDuration());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        List<TunnelInterfaceVO> tivo = new ArrayList<>();
        tivo.add(tivoA);
        tivo.add(tivoZ);
        //todo
        //vo.setTunnelInterfaceVOs(tivo);
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);


        dbf.persistAndRefresh(tivoA);
        dbf.persistAndRefresh(tivoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnel(msg.getId(),
                msg.getBandwidthOfferingUuid(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid(),
                vo);

    }

    @Transactional
    private void handle(APICreateTunnelManualMsg msg) {

        //保存数据
        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        vo.setUuid(Platform.getUuid());

        TunnelInterfaceVO tivoA = new TunnelInterfaceVO();
        tivoA.setUuid(Platform.getUuid());
        tivoA.setTunnelUuid(vo.getUuid());
        tivoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tivoA.setVlan(msg.getaVlan());
        tivoA.setSortTag("A");
        tivoA.setQinqState(msg.getQinqStateA());
        tivoA.setInterfaceVO(dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class));

        TunnelInterfaceVO tivoZ = new TunnelInterfaceVO();
        tivoZ.setUuid(Platform.getUuid());
        tivoZ.setTunnelUuid(vo.getUuid());
        tivoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tivoZ.setVlan(msg.getzVlan());
        tivoZ.setSortTag("Z");
        tivoZ.setQinqState(msg.getQinqStateZ());
        tivoZ.setInterfaceVO(dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class));

        //如果开启Qinq,需要指定内部vlan段
        if (msg.getQinqStateA() == TunnelQinqState.Enabled || msg.getQinqStateZ() == TunnelQinqState.Enabled) {
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

        //根据经纬度算距离
        NodeVO nvoA = dbf.findByUuid(msg.getNodeAUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(msg.getNodeZUuid(), NodeVO.class);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(), nvoA.getLatitude(), nvoZ.getLongtitude(), nvoZ.getLatitude()));

        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setDuration(msg.getDuration());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        List<TunnelInterfaceVO> tivo = new ArrayList<>();
        tivo.add(tivoA);
        tivo.add(tivoZ);
        //todo
        //vo.setTunnelInterfaceVOs(tivo);
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);


        dbf.persistAndRefresh(tivoA);
        dbf.persistAndRefresh(tivoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnel(msg.getId(),
                msg.getBandwidthOfferingUuid(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid(),
                vo);

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
        orderMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid()));
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
        APIUpdateTunnelExpireDateEvent evt = new APIUpdateTunnelExpireDateEvent(msg.getId());

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
            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.publish(evt);
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
        orderMsg.setExpiredTime(vo.getExpireDate());
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
        orderMsg.setExpiredTime(vo.getExpireDate());
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

    private void saveResourceOrderEffective(String orderUuid, String resourceUuid, String resourceType) {
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceUuid(resourceUuid);
        resourceOrderEffectiveVO.setResourceType(resourceType);
        resourceOrderEffectiveVO.setOrderUuid(orderUuid);
        dbf.persistAndRefresh(resourceOrderEffectiveVO);
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

    private void handle(APIQueryTunnelDetailForAlarmMsg msg){
        Map<String,Map<String,String>> detailMap = new HashMap<>();

        Map<String,String> map = new HashMap<>();
        for(String tunnelUuid:msg.getTunnelUuidList()){
            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).findValue();
            map.put("tunnelUuid",tunnel.getName());
            map.put("bandwidth",tunnel.getBandwidth().toString());

            List<TunnelInterfaceVO> interfaceList = Q.New(TunnelInterfaceVO.class).eq(TunnelInterfaceVO_.tunnelUuid,tunnelUuid).list();
            for(TunnelInterfaceVO vo : interfaceList){
                if("A".equals(vo.getSortTag()))
                    map.put("endpointAVlan",vo.getVlan().toString());
                else if("Z".equals(vo.getSortTag()))
                    map.put("endpointZVlan",vo.getVlan().toString());
            }

            //TODO: 等丁修改完成后取
            map.put("endpointAIp","endpointAIp");
            map.put("endpointZIp","endpointZIp");

            //detailList.add(map);
            detailMap.put(tunnelUuid,map);
        }

        APIQueryTunnelDetailForAlarmReply reply = new APIQueryTunnelDetailForAlarmReply();
        reply.setMap(detailMap);
        bus.reply(msg,reply);
    }

    @Transactional
    private void deleteTunnel(TunnelVO vo) {
        dbf.remove(vo);

        //删除对应的 TunnelInterfaceVO 和 QingqVO
        SimpleQuery<TunnelInterfaceVO> q = dbf.createQuery(TunnelInterfaceVO.class);
        q.add(TunnelInterfaceVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<TunnelInterfaceVO> tivList = q.list();
        if (tivList.size() > 0) {
            for (TunnelInterfaceVO tiv : tivList) {
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
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            updateTunnelFromOrderBuy(cmd);
                        }

                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.UN_SUBCRIBE.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
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
                    }
                });

        restf.registerSyncHttpCallHandler(OrderType.RENEW.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            updateTunnelFromOrderRenewOrSla(cmd);
                        }

                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.SLA_COMPENSATION.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            updateTunnelFromOrderRenewOrSla(cmd);
                        }

                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.UPGRADE.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            updateTunnelFromOrderModifyBandwidth(cmd);
                        }

                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.DOWNGRADE.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            updateTunnelFromOrderModifyBandwidth(cmd);
                        }

                        return null;
                    }
                });
        return true;
    }


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

    //获取物理接口的单价
    private List<ProductPriceUnit> getInterfacePriceUnit(String portOfferingUuid) {
        List<ProductPriceUnit> units = new ArrayList<>();
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setProductTypeCode(ProductType.PORT);
        unit.setCategoryCode(Category.PORT);
        unit.setAreaCode("DEFAULT");
        unit.setLineCode("DEFAULT");
        unit.setConfigCode(portOfferingUuid);
        units.add(unit);
        return units;
    }

    private List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid) {
        List<ProductPriceUnit> units = new ArrayList<ProductPriceUnit>();
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(Category.CITY);
        unit.setAreaCode("DEFAULT");
        unit.setLineCode("DEFAULT");
        unit.setConfigCode(bandwidthOfferingUuid);
        units.add(unit);
        return units;
    }

    //获取云专线的单价
    private List<ProductPriceUnit> getTunnelPriceUnitCopy(String bandwidthOfferingUuid,String nodeAUuid,String nodeZUuid,String innerEndpointUuid) {
        List<ProductPriceUnit> units = new ArrayList<ProductPriceUnit>();
        NodeVO nodeA = dbf.findByUuid(nodeAUuid,NodeVO.class);
        NodeVO nodeZ = dbf.findByUuid(nodeZUuid,NodeVO.class);
        String zoneCodeA = getZoneCode(nodeA.getUuid());
        String zoneCodeZ = getZoneCode(nodeZ.getUuid());
        if(innerEndpointUuid == null){  //国内互传
            Category category = null;
            String areaCode = null;
            String lineCode = null;
            ProductPriceUnit unit = new ProductPriceUnit();

            if(nodeA.getCity() == nodeZ.getCity()){  //同城
                category = Category.CITY;
                areaCode = "DEFAULT";
                lineCode = "DEFAULT";
            }else if(zoneCodeA != null && zoneCodeZ != null && zoneCodeA == zoneCodeZ){ //同区域
                category = Category.REGION;
                areaCode = zoneCodeA;
                lineCode = "DEFAULT";
            }else{                      //长传
                category = Category.LONG;
                areaCode = "DEFAULT";
                lineCode = "DEFAULT";
            }
            unit.setProductTypeCode(ProductType.TUNNEL);
            unit.setCategoryCode(category);
            unit.setAreaCode(areaCode);
            unit.setLineCode(lineCode);
            unit.setConfigCode(bandwidthOfferingUuid);

            units.add(unit);
        }else{                          //跨国 或者 国外到国外 或者 国外内部

        }

        return units;
    }

    //根据节点找到所属区域
    private String getZoneCode(String nodeUuid){
        String zoneCode = null;
        ZoneNodeRefVO zoneNodeRefVO = Q.New(ZoneNodeRefVO.class)
                .eq(ZoneNodeRefVO_.nodeUuid,nodeUuid)
                .find();
        if(zoneNodeRefVO != null){
            ZoneVO zoneVO = dbf.findByUuid(zoneNodeRefVO.getZoneUuid(),ZoneVO.class);
            zoneCode = zoneVO.getCode();
        }
        return zoneCode;
    }

    private Future<Void> cleanExpiredProductThread = null;
    private int cleanExpiredProductInterval;

    private void startCleanExpiredProduct() {
        cleanExpiredProductInterval = CoreGlobalProperty.CLEAN_EXPIRED_PRODUCT_INTERVAL;
        if (cleanExpiredProductThread != null) {
            cleanExpiredProductThread.cancel(true);
        }

        cleanExpiredProductThread = thdf.submitPeriodicTask(new CleanExpiredProductThread(), TimeUnit.DAYS.toMillis(1));
        logger.debug(String
                .format("security group cleanExpiredProductThread starts[cleanExpiredProductInterval: %s day]", cleanExpiredProductInterval));
    }

    private List<TunnelVO> tunnelVOs = new ArrayList<>();

    private class CleanExpiredProductThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.DAYS;
        }

        @Override
        public long getInterval() {
            return cleanExpiredProductInterval;
        }

        @Override
        public String getName() {
            return "clean-expired-product-" + Platform.getManagementServerId();
        }

        @Transactional
        private List<TunnelVO> getTunnels() {
            return Q.New(TunnelVO.class)
                    .lte(TunnelVO_.expiredDate, Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
                    .list();
        }


        @Override
        public void run() {
            try {
                tunnelVOs.clear();
                tunnelVOs = getTunnels();
                logger.debug("delete expired tunnel.");
                if (tunnelVOs.isEmpty())
                    return;
                List<DeleteTunnelMsg> msgs = new ArrayList<>();
                for (TunnelVO vo : tunnelVOs) {
                    if (vo.getState() == TunnelState.Unpaid) {
                        deleteTunnel(vo);
                    } else {
                        TaskResourceVO task = newTaskResourceVO(vo, TaskType.Delete);
                        DeleteTunnelMsg msg = new DeleteTunnelMsg();
                        msg.setTaskUuid(task.getUuid());
                        msg.setTunnelUuid(vo.getUuid());
                        bus.makeTargetServiceIdByResourceUuid(msg, TunnelConstant.SERVICE_ID, vo.getUuid());
                        msgs.add(msg);
                    }
                    tunnelVOs.remove(vo);
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
        }
        return msg;
    }

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

    private void validate(APICreateInterfaceMsg msg) {
        SwitchPortType type = Q.New(PortOfferingVO.class)
                .eq(PortOfferingVO_.uuid, msg.getPortOfferingUuid())
                .select(PortOfferingVO_.type).findValue();
        //类型是否支持
        List<SwitchPortType> types = getPortTypeByEndpoint(msg.getEndpointUuid());
        if (!types.contains(type))
            throw new ApiMessageInterceptionException(
                    argerr("该连接点[uuid:%s]下的端口[type:%s]已用完！", msg.getEndpointUuid(), type));
        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
        //判断同一个用户的接口名称是否已经存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ", msg.getName()));
        }
    }

    private void validate(APICreateInterfaceManualMsg msg) {
        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
        //判断同一个用户的接口名称是否已经存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ", msg.getName()));
        }
    }

    private void validate(APIUpdateInterfaceMsg msg) {
        //判断同一个用户的网络名称是否已经存在
        if (msg.getName() != null) {
            SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
            q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(InterfaceVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ", msg.getName()));
            }
        }

    }

    private void validate(APIUpdateInterfaceExpireDateMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APIDeleteInterfaceMsg msg) {
        //判断云专线下是否有该物理接口
        InterfaceVO interfaceVO = dbf.findByUuid(msg.getUuid(),InterfaceVO.class);
        String sql = "select count(a.uuid) from TunnelSwitchVO a,TunnelVO b " +
                "where b.uuid = a.tunnelUuid " +
                "and b.accountUuid = :accountUuid " +
                "and a.switchPortUuid = :switchPortUuid";
        TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
        vq.setParameter("accountUuid", msg.getAccountUuid());
        vq.setParameter("switchPortUuid", interfaceVO.getSwitchPortUuid());
        Long count = vq.getSingleResult();
        if (count > 0) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,interface is being used!"));
        }

        //判断该产品是否有未完成订单
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APICreateTunnelMsg msg) {
        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getBandwidthOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }
        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(msg.getEndpointPointAUuid(), msg.getEndpointPointZUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }
    }

    private void validate(APICreateTunnelManualMsg msg) {
        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getBandwidthOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }

        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(msg.getEndpointPointAUuid(), msg.getEndpointPointZUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }

        TunnelStrategy ts = new TunnelStrategy();
        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuidA = ts.findSwitchByInterface(msg.getInterfaceAUuid());
        String switchUuidZ = ts.findSwitchByInterface(msg.getInterfaceZUuid());

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanListA = ts.findSwitchVlanBySwitch(switchUuidA);
        List<SwitchVlanVO> vlanListZ = ts.findSwitchVlanBySwitch(switchUuidZ);

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlansA = ts.fingAllocateVlanBySwitch(switchUuidA);
        List<Integer> allocatedVlansZ = ts.fingAllocateVlanBySwitch(switchUuidZ);

        //判断外部VLAN是否在该虚拟交换机的VLAN段中
        Boolean innerA = false;
        for (SwitchVlanVO switchVlanVO : vlanListA) {
            if (msg.getaVlan() >= switchVlanVO.getStartVlan() && msg.getaVlan() <= switchVlanVO.getEndVlan()) {
                innerA = true;
                break;
            }
        }
        if (!innerA) {
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        Boolean innerZ = false;
        for (SwitchVlanVO switchVlanVO : vlanListZ) {
            if (msg.getzVlan() >= switchVlanVO.getStartVlan() && msg.getzVlan() <= switchVlanVO.getEndVlan()) {
                innerZ = true;
                break;
            }
        }
        if (!innerZ) {
            throw new ApiMessageInterceptionException(argerr("zvlan not in switchVlan"));
        }

        //判断外部vlan是否可用
        //给A端口vlan验证
        if (allocatedVlansA.contains(msg.getaVlan())) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", msg.getaVlan()));
        }

        //给Z端口vlan验证
        if (allocatedVlansZ.contains(msg.getzVlan())) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", msg.getzVlan()));
        }

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select tunnelUuid from TunnelInterfaceVO where interfaceUuid = :interfaceUuid and qinqState = 'Enabled') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if (msg.getVlanSegment() != null) {
            if (msg.getQinqStateA() == TunnelQinqState.Enabled) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("interfaceUuid", msg.getInterfaceAUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
            if (msg.getQinqStateZ() == TunnelQinqState.Enabled) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("interfaceUuid", msg.getInterfaceZUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }
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
                "where a.tunnelUuid in (select tunnelUuid from TunnelInterfaceVO where interfaceUuid = :interfaceUuid and qinqState = 'Enabled') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        TunnelInterfaceVO tunnelInterfaceA = Q.New(TunnelInterfaceVO.class)
                .eq(TunnelInterfaceVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelInterfaceVO_.sortTag, "A")
                .find();
        TunnelInterfaceVO tunnelInterfaceZ = Q.New(TunnelInterfaceVO.class)
                .eq(TunnelInterfaceVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelInterfaceVO_.sortTag, "Z")
                .find();
        if (tunnelInterfaceA.getQinqState() == TunnelQinqState.Enabled) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("interfaceUuid", tunnelInterfaceA.getInterfaceUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }
        if (tunnelInterfaceZ.getQinqState() == TunnelQinqState.Enabled) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("interfaceUuid", tunnelInterfaceZ.getInterfaceUuid());
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

}
