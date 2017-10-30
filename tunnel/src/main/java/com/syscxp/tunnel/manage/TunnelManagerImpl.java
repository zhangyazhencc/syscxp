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
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.TunnelMonitorState;
import com.syscxp.header.tunnel.TunnelState;
import com.syscxp.header.tunnel.TunnelStatus;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.header.switchs.*;
import com.syscxp.tunnel.header.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    private MonitorManagerImpl monitorManagerImpl;

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
        base.handleMessage((Message) msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateInterfaceMsg) {
            handle((APICreateInterfaceMsg) msg);
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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    /**
     * 自动获取 VSI
     */
    private Integer getVsiAuto() {

        GLock glock = new GLock("maxvsi", 120);
        glock.lock();

        Integer vsi = null;
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
        APICreateOrderReply reply;
        try {
            APIReply apiReply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);
            if (!apiReply.isSuccess())
                return null;
            reply = apiReply.castReply();

        } catch (Exception e) {
            return null;
        }
        return reply.getInventory();
    }

    private void handle(APICreateInterfaceMsg msg) {
        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());

        //保存数据，分配资源
        InterfaceVO vo = new InterfaceVO();

        //分配资源:策略分配端口
        TunnelStrategy ts = new TunnelStrategy();
        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getEndpointUuid(), msg.getPortAttribute(), msg.getPortType());
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
        vo.setExpireDate(null);
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);

        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setProductDescription("no description");
        orderMsg.setUnits(msg.getUnits());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("InterfaceVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);
            //状态修改已支付，生成到期时间
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            if (msg.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));
            } else if (msg.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(msg.getDuration())));
            }

            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //付款失败
            vo.setExpireDate(dbf.getCurrentSqlTime());
            vo = dbf.updateAndRefresh(vo);
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
        vo.setExpireDate(null);
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);


        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setProductDescription("no description");
        orderMsg.setUnits(msg.getUnits());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("InterfaceVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);
            //状态修改已支付，生成到期时间
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            if (msg.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(msg.getDuration())));
            } else if (msg.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(msg.getDuration())));
            }

            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //付款失败
            vo.setExpireDate(dbf.getCurrentSqlTime());
            vo = dbf.updateAndRefresh(vo);
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
        LocalDateTime newTime = vo.getExpireDate().toLocalDateTime();
        OrderInventory orderInventory = null;
        switch (msg.getType()) {
            case RENEW://续费
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
                renewOrderMsg.setProductUuid(vo.getUuid());
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
                renewOrderMsg.setAccountUuid(msg.getAccountUuid());
                renewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                renewOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(renewOrderMsg);
                if (msg.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                    newTime = newTime.plusMonths(msg.getDuration());
                } else if (msg.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                    newTime = newTime.plusYears(msg.getDuration());
                }
                break;
            case SLA_COMPENSATION://赔偿
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                        new APICreateSLACompensationOrderMsg();
                slaCompensationOrderMsg.setProductUuid(vo.getUuid());
                slaCompensationOrderMsg.setProductName(vo.getName());
                slaCompensationOrderMsg.setProductDescription("no description");
                slaCompensationOrderMsg.setProductType(ProductType.PORT);
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(slaCompensationOrderMsg);
                newTime = newTime.plusDays(msg.getDuration());
                break;
        }

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("InterfaceVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(msg.getProductChargeModel());
            vo.setExpireDate(Timestamp.valueOf(newTime));

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
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setProductName(vo.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("InterfaceVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);
            //删除产品
            dbf.remove(vo);
            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //退订失败
            evt.setError(errf.stringToOperationError("退订失败"));
        }

        bus.publish(evt);
    }

    private void afterCreateTunnel(APICreateTunnelMsg msg, TunnelVO vo) {
        APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setUnits(msg.getUnits());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setProductDescription("no description");

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
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
        resourceOrderEffectiveVO.setResourceType("TunnelVO");
        resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
        resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);

        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        vo = dbf.updateAndRefresh(vo);

        //创建任务
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setResourceUuid(vo.getUuid());
        taskResourceVO.setResourceType("TunnelVO");
        taskResourceVO.setTaskType(TaskType.Create);
        taskResourceVO.setBody(null);
        taskResourceVO.setResult(null);
        taskResourceVO.setStatus(TaskStatus.Preexecute);
        taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

        CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
        createTunnelMsg.setTunnelUuid(vo.getUuid());
        createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeTargetServiceIdByResourceUuid(createTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
        bus.send(createTunnelMsg);

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void afterCreateTunnelManual(APICreateTunnelManualMsg msg, TunnelVO vo) {
        APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductChargeModel(vo.getProductChargeModel());
        orderMsg.setDuration(vo.getDuration());
        orderMsg.setUnits(msg.getUnits());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setProductDescription("no description");

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
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
        resourceOrderEffectiveVO.setResourceType("TunnelVO");
        resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
        resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);

        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        vo = dbf.updateAndRefresh(vo);

        //创建任务
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setResourceUuid(vo.getUuid());
        taskResourceVO.setResourceType("TunnelVO");
        taskResourceVO.setTaskType(TaskType.Create);
        taskResourceVO.setBody(null);
        taskResourceVO.setResult(null);
        taskResourceVO.setStatus(TaskStatus.Preexecute);
        taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

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
        vo.setMonitorCidr(msg.getMonitorCidr());
        vo.setName(msg.getName());
        vo.setBandwidth(msg.getBandwidth());
        vo.setDuration(msg.getDuration());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        List<TunnelInterfaceVO> tivo = new ArrayList<TunnelInterfaceVO>();
        tivo.add(tivoA);
        tivo.add(tivoZ);
        vo.setTunnelInterfaceVOs(tivo);
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);


        tivoA = dbf.persistAndRefresh(tivoA);
        tivoZ = dbf.persistAndRefresh(tivoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnel(msg, vo);

    }

    @Transactional
    private void handle(APICreateTunnelManualMsg msg) {

        APICreateTunnelManualEvent evt = new APICreateTunnelManualEvent(msg.getId());
        //保存数据
        TunnelVO vo = new TunnelVO();

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
        vo.setBandwidth(msg.getBandwidth());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        List<TunnelInterfaceVO> tivo = new ArrayList<TunnelInterfaceVO>();
        tivo.add(tivoA);
        tivo.add(tivoZ);
        vo.setTunnelInterfaceVOs(tivo);
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);


        tivoA = dbf.persistAndRefresh(tivoA);
        tivoZ = dbf.persistAndRefresh(tivoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnelManual(msg, vo);

    }

    private void handle(APIUpdateTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean update = false;


        if (msg.getBandwidth() != null) {
            vo.setBandwidth(msg.getBandwidth());
            update = true;
        }
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
        if (msg.getExpireDate() != null) {
            vo.setExpireDate(msg.getExpireDate());
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

        //调整次数记录表
        TunnelMotifyRecordVO record = new TunnelMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setTunnelUuid(vo.getUuid());
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setMotifyType(msg.getBandwidth() > vo.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DEMOTION);
        dbf.persistAndRefresh(record);

        //调用支付-调整带宽
        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductDescription(msg.getBandwidth().toString());
        orderMsg.setUnits(msg.getUnits());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("TunnelVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);

            vo.setBandwidth(msg.getBandwidth());
            vo = dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = new TaskResourceVO();
            taskResourceVO.setUuid(Platform.getUuid());
            taskResourceVO.setResourceUuid(vo.getUuid());
            taskResourceVO.setResourceType("TunnelVO");
            taskResourceVO.setTaskType(TaskType.ModifyBandwidth);
            taskResourceVO.setBody(null);
            taskResourceVO.setResult(null);
            taskResourceVO.setStatus(TaskStatus.Preexecute);
            taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

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
        LocalDateTime newTime = vo.getExpireDate().toLocalDateTime();
        OrderInventory orderInventory = null;
        switch (msg.getType()) {
            case RENEW://续费
                APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
                renewOrderMsg.setProductUuid(vo.getUuid());
                renewOrderMsg.setDuration(msg.getDuration());
                renewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
                renewOrderMsg.setAccountUuid(msg.getAccountUuid());
                renewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                renewOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(renewOrderMsg);
                if (msg.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                    newTime = newTime.plusMonths(msg.getDuration());
                } else if (msg.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                    newTime = newTime.plusYears(msg.getDuration());
                }
                break;
            case SLA_COMPENSATION://赔偿
                APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                        new APICreateSLACompensationOrderMsg();
                slaCompensationOrderMsg.setProductUuid(vo.getUuid());
                slaCompensationOrderMsg.setProductName(vo.getName());
                slaCompensationOrderMsg.setProductDescription("no description");
                slaCompensationOrderMsg.setProductType(ProductType.TUNNEL);
                slaCompensationOrderMsg.setDuration(msg.getDuration());
                slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
                slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
                slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());

                orderInventory = createOrder(slaCompensationOrderMsg);
                newTime = newTime.plusDays(msg.getDuration());
                break;
        }

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("TunnelVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(msg.getProductChargeModel());
            vo.setExpireDate(Timestamp.valueOf(newTime));

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

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
            resourceOrderEffectiveVO.setUuid(Platform.getUuid());
            resourceOrderEffectiveVO.setResourceUuid(vo.getUuid());
            resourceOrderEffectiveVO.setResourceType("TunnelVO");
            resourceOrderEffectiveVO.setOrderUuid(orderInventory.getUuid());
            resourceOrderEffectiveVO = dbf.persistAndRefresh(resourceOrderEffectiveVO);

            vo.setAccountUuid(null);
            dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = new TaskResourceVO();
            taskResourceVO.setUuid(Platform.getUuid());
            taskResourceVO.setResourceUuid(vo.getUuid());
            taskResourceVO.setResourceType("TunnelVO");
            taskResourceVO.setTaskType(TaskType.Delete);
            taskResourceVO.setBody(null);
            taskResourceVO.setResult(null);
            taskResourceVO.setStatus(TaskStatus.Preexecute);
            taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

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

        deleteTunnel(vo);

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
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

    private boolean orderIsExist(String orderUuid){
        return Q.New(ResourceOrderEffectiveVO.class)
                .eq(ResourceOrderEffectiveVO_.orderUuid,orderUuid)
                .isExists();
    }

    private void updateTunnelFromOrderRenewOrSla(OrderCallbackCmd cmd) {
        if (cmd.getProductType() == ProductType.PORT) {
            InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
            if (!orderIsExist(cmd.getOrderUuid())) {
                if (cmd.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(cmd.getDuration())));
                } else if (cmd.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(cmd.getDuration())));
                } else if(cmd.getProductChargeModel() == ProductChargeModel.BY_DAY){
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusDays(cmd.getDuration())));
                }

                vo.setDuration(cmd.getDuration());
                vo.setProductChargeModel(cmd.getProductChargeModel());
                dbf.updateAndRefresh(vo);
            }

        } else if (cmd.getProductType() == ProductType.TUNNEL) {
            TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
            if (!orderIsExist(cmd.getOrderUuid())) {
                if (cmd.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(cmd.getDuration())));
                } else if (cmd.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(cmd.getDuration())));
                } else if(cmd.getProductChargeModel() == ProductChargeModel.BY_DAY){
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusDays(cmd.getDuration())));
                }
                vo.setDuration(cmd.getDuration());
                vo.setProductChargeModel(cmd.getProductChargeModel());
                dbf.updateAndRefresh(vo);
            }
        }
    }

    private void updateTunnelFromOrderBuy(OrderCallbackCmd cmd) {
        if (cmd.getProductType() == ProductType.PORT) {
            InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);

            if (!orderIsExist(cmd.getOrderUuid())) {
                vo.setAccountUuid(vo.getOwnerAccountUuid());
                vo.setState(InterfaceState.Paid);
                if (cmd.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(cmd.getDuration())));
                } else if (cmd.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                    vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(cmd.getDuration())));
                }
                dbf.updateAndRefresh(vo);
            }

        } else if (cmd.getProductType() == ProductType.TUNNEL) {
            TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
            if (!orderIsExist(cmd.getOrderUuid())) {
                vo.setAccountUuid(vo.getOwnerAccountUuid());
                vo.setState(TunnelState.Deploying);
                vo.setStatus(TunnelStatus.Connecting);
                dbf.updateAndRefresh(vo);

                //创建任务
                TaskResourceVO taskResourceVO = new TaskResourceVO();
                taskResourceVO.setUuid(Platform.getUuid());
                taskResourceVO.setResourceUuid(vo.getUuid());
                taskResourceVO.setResourceType("TunnelVO");
                taskResourceVO.setTaskType(TaskType.Create);
                taskResourceVO.setBody(null);
                taskResourceVO.setResult(null);
                taskResourceVO.setStatus(TaskStatus.Preexecute);
                taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

                CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
                createTunnelMsg.setTunnelUuid(vo.getUuid());
                createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeTargetServiceIdByResourceUuid(createTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
                bus.send(createTunnelMsg);
            }

        }
    }

    @Override
    public boolean start() {
        restf.registerSyncHttpCallHandler(OrderType.BUY.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (cmd.getProductType() == ProductType.PORT) {
                            updateTunnelFromOrderBuy(cmd);
                        } else if (cmd.getProductType() == ProductType.TUNNEL) {
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
                            if (vo != null && vo.getAccountUuid() != null) {
                                vo.setAccountUuid(null);
                                dbf.updateAndRefresh(vo);

                                //创建任务
                                TaskResourceVO taskResourceVO = new TaskResourceVO();
                                taskResourceVO.setUuid(Platform.getUuid());
                                taskResourceVO.setResourceUuid(vo.getUuid());
                                taskResourceVO.setResourceType("TunnelVO");
                                taskResourceVO.setTaskType(TaskType.Delete);
                                taskResourceVO.setBody(null);
                                taskResourceVO.setResult(null);
                                taskResourceVO.setStatus(TaskStatus.Preexecute);
                                taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

                                DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
                                deleteTunnelMsg.setTunnelUuid(vo.getUuid());
                                deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                                bus.makeTargetServiceIdByResourceUuid(deleteTunnelMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
                                bus.send(deleteTunnelMsg);
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
                        updateTunnelFromOrderRenewOrSla(cmd);
                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.SLA_COMPENSATION.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        updateTunnelFromOrderRenewOrSla(cmd);
                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.UPGRADE.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));

                        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            vo.setBandwidth(Long.valueOf(cmd.getProductDescription()));
                            dbf.updateAndRefresh(vo);

                            //创建任务
                            TaskResourceVO taskResourceVO = new TaskResourceVO();
                            taskResourceVO.setUuid(Platform.getUuid());
                            taskResourceVO.setResourceUuid(vo.getUuid());
                            taskResourceVO.setResourceType("TunnelVO");
                            taskResourceVO.setTaskType(TaskType.ModifyBandwidth);
                            taskResourceVO.setBody(null);
                            taskResourceVO.setResult(null);
                            taskResourceVO.setStatus(TaskStatus.Preexecute);
                            taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

                            ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
                            modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
                            modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
                            bus.makeTargetServiceIdByResourceUuid(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
                            bus.send(modifyTunnelBandwidthMsg);
                        }
                        return null;
                    }
                });
        restf.registerSyncHttpCallHandler(OrderType.DOWNGRADE.toString(), OrderCallbackCmd.class,
                new SyncHttpCallHandler<OrderCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(OrderCallbackCmd cmd) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));

                        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            vo.setBandwidth(Long.valueOf(cmd.getProductDescription()));
                            dbf.updateAndRefresh(vo);

                            //创建任务
                            TaskResourceVO taskResourceVO = new TaskResourceVO();
                            taskResourceVO.setUuid(Platform.getUuid());
                            taskResourceVO.setResourceUuid(vo.getUuid());
                            taskResourceVO.setResourceType("TunnelVO");
                            taskResourceVO.setTaskType(TaskType.ModifyBandwidth);
                            taskResourceVO.setBody(null);
                            taskResourceVO.setResult(null);
                            taskResourceVO.setStatus(TaskStatus.Preexecute);
                            taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

                            ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
                            modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
                            modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
                            bus.makeTargetServiceIdByResourceUuid(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID, vo.getUuid());
                            bus.send(modifyTunnelBandwidthMsg);
                        }
                        return null;
                    }
                });
        return true;
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
        }
        return msg;
    }

    private void validate(APICreateInterfaceMsg msg) {
        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(msg.getUnits());
        APIReply rsp = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!rsp.isSuccess())
            throw new ApiMessageInterceptionException(
                    argerr("查询价格失败.", msg.getAccountUuid()));
        APIGetProductPriceReply reply = (APIGetProductPriceReply) rsp;
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
        priceMsg.setProductChargeModel(ProductChargeModel.BY_MONTH);
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(msg.getUnits());
        APIReply rsp = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!rsp.isSuccess())
            throw new ApiMessageInterceptionException(
                    argerr("查询价格失败.", msg.getAccountUuid()));
        APIGetProductPriceReply reply = (APIGetProductPriceReply) rsp;
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
        //判断该产品是否有未完成订单
        
    }

    private void validate(APIDeleteInterfaceMsg msg) {
        //判断云专线下是否有该物理接口
        SimpleQuery<TunnelInterfaceVO> q = dbf.createQuery(TunnelInterfaceVO.class);
        q.add(TunnelInterfaceVO_.interfaceUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,interface is being used!"));
        }
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
        if (msg.getEndpointPointAUuid() == msg.getEndpointPointZUuid()) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }
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
        if (msg.getEndpointPointAUuid() == msg.getEndpointPointZUuid()) {
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
        if (innerA == false) {
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        Boolean innerZ = false;
        for (SwitchVlanVO switchVlanVO : vlanListZ) {
            if (msg.getzVlan() >= switchVlanVO.getStartVlan() && msg.getzVlan() <= switchVlanVO.getEndVlan()) {
                innerZ = true;
                break;
            }
        }
        if (innerZ == false) {
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
    }

    private void validate(APIDeleteTunnelMsg msg) {
    }

    private void validate(APIDeleteForciblyTunnelMsg msg) {
    }

}
