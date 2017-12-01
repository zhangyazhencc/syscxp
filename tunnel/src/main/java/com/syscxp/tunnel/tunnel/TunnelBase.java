package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.Q;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.*;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by DCY on 2017/12/1
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelBase {
    private static final CLogger logger = Utils.getLogger(TunnelBase.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;

    /**
     * 自动获取 VSI
     */
    public Integer getVsiAuto() {

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
    public OrderInventory createOrder(APICreateOrderMsg orderMsg) {
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

    public List<OrderInventory> createBuyOrder(APICreateBuyOrderMsg orderMsg) {
        try {
            APICreateBuyOrderReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);

            if (reply.isSuccess()) {
                return reply.getInventories();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建订单, %s", e.getMessage()), e);
        }
        return Collections.emptyList();
    }

    /**
     * 付款成功,记录生效订单
     */
    public void saveResourceOrderEffective(String orderUuid, String resourceUuid, String resourceType) {
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceUuid(resourceUuid);
        resourceOrderEffectiveVO.setResourceType(resourceType);
        resourceOrderEffectiveVO.setOrderUuid(orderUuid);
        dbf.persistAndRefresh(resourceOrderEffectiveVO);
    }

    /**
     * 创建云专线 支付成功创建下发任务
     */
    public TaskResourceVO newTaskResourceVO(TunnelVO vo, TaskType taskType) {
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setAccountUuid(vo.getOwnerAccountUuid());
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
    public void createTunnelSwitchPortForAbroad(String innerConnectedEndpointUuid, TunnelVO vo, boolean isBInner) {
        TunnelStrategy ts = new TunnelStrategy();

        //通过互联连接点找到内联交换机和内联端口
        SwitchVO innerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.INNER)
                .find();
        SwitchPortVO innerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, innerSwitch.getUuid())
                .find();
        //通过互联连接点找到外联交换机和外联端口
        SwitchVO outerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.OUTER)
                .find();
        SwitchPortVO outerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, outerSwitch.getUuid())
                .find();
        //获取互联设备的VLAN
        Integer innerVlan = ts.getVlanBySwitch(innerSwitch.getUuid());

        TunnelSwitchPortVO tsvoB = new TunnelSwitchPortVO();
        TunnelSwitchPortVO tsvoC = new TunnelSwitchPortVO();

        if (isBInner) {
            tsvoB.setUuid(Platform.getUuid());
            tsvoB.setTunnelUuid(vo.getUuid());
            tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoB.setInterfaceUuid(null);
            tsvoB.setSwitchPortUuid(innerSwitchPort.getUuid());
            tsvoB.setType(NetworkType.TRUNK);
            tsvoB.setVlan(innerVlan);
            tsvoB.setSortTag("B");


            tsvoC.setUuid(Platform.getUuid());
            tsvoC.setTunnelUuid(vo.getUuid());
            tsvoC.setInterfaceUuid(null);
            tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoC.setSwitchPortUuid(outerSwitchPort.getUuid());
            tsvoC.setType(NetworkType.TRUNK);
            tsvoC.setVlan(innerVlan);
            tsvoC.setSortTag("C");

        } else {
            tsvoB.setUuid(Platform.getUuid());
            tsvoB.setTunnelUuid(vo.getUuid());
            tsvoB.setInterfaceUuid(null);
            tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoB.setSwitchPortUuid(outerSwitchPort.getUuid());
            tsvoB.setType(NetworkType.TRUNK);
            tsvoB.setVlan(innerVlan);
            tsvoB.setSortTag("B");

            tsvoC.setUuid(Platform.getUuid());
            tsvoC.setTunnelUuid(vo.getUuid());
            tsvoC.setInterfaceUuid(null);
            tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoC.setSwitchPortUuid(innerSwitchPort.getUuid());
            tsvoC.setType(NetworkType.TRUNK);
            tsvoC.setVlan(innerVlan);
            tsvoC.setSortTag("C");

        }
        dbf.persistAndRefresh(tsvoB);
        dbf.persistAndRefresh(tsvoC);

    }

    /**
     * 获取到期时间
     */
    public Timestamp getExpireDate(Timestamp oldTime, ProductChargeModel chargeModel, int duration) {
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
    public APICreateOrderMsg getOrderMsgForInterface(InterfaceVO vo, NotifyCallBackData callBack) {
        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setDescriptionData(getDescriptionForInterface(vo));
        orderMsg.setAccountUuid(vo.getOwnerAccountUuid());
        orderMsg.setCallBackData(RESTApiDecoder.dump(callBack));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        return orderMsg;
    }

    /**
     * 获取物理接口单价
     */
    public List<ProductPriceUnit> getInterfacePriceUnit(String portOfferingUuid) {
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

    /**
     * 获取云专线单价
     */
    public List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid, String nodeAUuid, String nodeZUuid, String innerEndpointUuid) {
        TunnelDbBase tunnelDbBase = new TunnelDbBase();

        List<ProductPriceUnit> units = new ArrayList<>();
        NodeVO nodeA = dbf.findByUuid(nodeAUuid, NodeVO.class);
        NodeVO nodeZ = dbf.findByUuid(nodeZUuid, NodeVO.class);
        String zoneUuidA = tunnelDbBase.getZoneUuid(nodeA.getUuid());
        String zoneUuidZ = tunnelDbBase.getZoneUuid(nodeZ.getUuid());
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
            String zoneUuidB = tunnelDbBase.getZoneUuid(nodeB.getUuid());

            if (nodeA.getCountry().equals("CHINA")) {
                ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeA, nodeB, zoneUuidA, zoneUuidB);
                ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeZ);

                units.add(unitInner);
                units.add(unitOuter);
            } else {
                ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeZ, nodeB, zoneUuidZ, zoneUuidB);
                ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeA);

                units.add(unitInner);
                units.add(unitOuter);
            }

        }
        return units;
    }

    /**
     * 获取云专线单价--国内互传单价
     */
    public ProductPriceUnit getTunnelPriceUnitCN(String bandwidthOfferingUuid, NodeVO nodeA, NodeVO nodeZ, String zoneUuidA, String zoneUuidZ) {
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
    public ProductPriceUnit getTunnelPriceUnitCNToAb(String bandwidthOfferingUuid, NodeVO nodeB, NodeVO nodeZ) {
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
    public ProductPriceUnit getTunnelPriceUnitAb(String bandwidthOfferingUuid, NodeVO nodeA, NodeVO nodeZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(Category.ABROAD);
        unit.setAreaCode("ABROAD");
        unit.setLineCode(nodeA.getCountry() + "/" + nodeZ.getCountry());
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 获取interface Description
     */
    public String getDescriptionForInterface(InterfaceVO vo) {
        DescriptionData data = new DescriptionData();
        data.add(new DescriptionItem("name", vo.getName()));
        data.add(new DescriptionItem("NetworkType", vo.getType().toString()));
        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, vo.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).findValue();
        data.add(new DescriptionItem("PortType", portType));

        return JSONObjectUtil.toJsonString(data);
    }

    /**
     * 获取tunnel Description
     */
    public String getDescriptionForTunnel(TunnelVO vo) {
        DescriptionData data = new DescriptionData();

        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        String endpointNameA = dbf.findByUuid(tunnelSwitchPortVOA.getEndpointUuid(), EndpointVO.class).getName();
        String endpointNameZ = dbf.findByUuid(tunnelSwitchPortVOZ.getEndpointUuid(), EndpointVO.class).getName();

        data.add(new DescriptionItem("endpointNameA", endpointNameA));
        data.add(new DescriptionItem("endpointNameZ", endpointNameZ));
        data.add(new DescriptionItem("bandwidth", vo.getBandwidth().toString()));

        return JSONObjectUtil.toJsonString(data);
    }

    /**
     * 手动创建Tunnel的订单
     */
    public ProductInfoForOrder createBuyOrderForTunnelManual(TunnelVO vo, APICreateTunnelManualMsg msg) {
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);

        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.TUNNEL);
        order.setProductChargeModel(vo.getProductChargeModel());
        order.setProductName(vo.getName());
        order.setDuration(vo.getDuration());
        order.setAccountUuid(vo.getOwnerAccountUuid());
        order.setOpAccountUuid(msg.getSession().getAccountUuid());
        order.setDescriptionData(getDescriptionForTunnel(vo));
        order.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(), evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    /**
     * 创建Tunnel的订单
     */
    public ProductInfoForOrder createBuyOrderForTunnel(TunnelVO vo, APICreateTunnelMsg msg) {
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.TUNNEL);
        order.setProductChargeModel(vo.getProductChargeModel());
        order.setDuration(vo.getDuration());
        order.setProductName(vo.getName());
        order.setAccountUuid(vo.getOwnerAccountUuid());
        order.setOpAccountUuid(msg.getSession().getAccountUuid());
        order.setDescriptionData(getDescriptionForTunnel(vo));
        order.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(), evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    /**
     * 创建interface的订单
     */
    public ProductInfoForOrder createBuyOrderForInterface(InterfaceVO vo, String portOfferingUuid, NotifyCallBackData callBack) {
        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductChargeModel(vo.getProductChargeModel());
        order.setDuration(vo.getDuration());
        order.setProductName(vo.getName());
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.PORT);
        order.setDescriptionData(getDescriptionForInterface(vo));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setUnits(getInterfacePriceUnit(portOfferingUuid));
        order.setAccountUuid(vo.getOwnerAccountUuid());

        return order;
    }
}
