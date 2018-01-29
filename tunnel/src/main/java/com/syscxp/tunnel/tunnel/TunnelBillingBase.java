package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.*;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.SizeUnit;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by DCY on 2017/12/1
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelBillingBase {
    private static final CLogger logger = Utils.getLogger(TunnelBillingBase.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;

    /**
     * 调用支付-单个产品
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

    /**
     * 调用支付-多产品一起支付
     */
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
     * 调用支付-最后一公里
     */
    public OrderInventory createOrderForEdgeLine(APICreateBuyEdgeLineOrderMsg orderMsg) {
        try {
            APICreateBuyEdgeLineOrderReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);

            if (reply.isSuccess()) {
                return reply.getInventory();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建订单, %s", e.getMessage()), e);
        }
        return null;
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
     * 获取物理接口单价--独享
     */
    public List<ProductPriceUnit> getInterfacePriceUnit(String portOfferingUuid) {
        List<ProductPriceUnit> units = new ArrayList<>();
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setProductTypeCode(ProductType.PORT);
        unit.setCategoryCode(Category.EXCLUSIVE);
        unit.setAreaCode("DEFAULT");
        unit.setLineCode("DEFAULT");
        unit.setConfigCode(portOfferingUuid);
        units.add(unit);
        return units;
    }

    /**
     * 获取云专线单价
     */
    public List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid,
                                                     String interfaceUuidA,
                                                     String interfaceUuidZ,
                                                     EndpointVO evoA,
                                                     EndpointVO evoZ,
                                                     String innerEndpointUuid) {
        TunnelBase tunnelBase = new TunnelBase();

        return getTunnelPriceUnit(bandwidthOfferingUuid
                , tunnelBase.isShareForInterface(interfaceUuidA)
                , tunnelBase.isShareForInterface(interfaceUuidZ)
                , evoA
                , evoZ
                , innerEndpointUuid);
    }

    public List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid,
                                                     boolean isShareInterfaceUuidA,
                                                     boolean isShareInterfaceUuidZ,
                                                     EndpointVO evoA,
                                                     EndpointVO evoZ,
                                                     String innerEndpointUuid) {
        TunnelBase tunnelBase = new TunnelBase();

        List<ProductPriceUnit> units = new ArrayList<>();
        NodeVO nodeA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
        NodeVO nodeZ = dbf.findByUuid(evoZ.getNodeUuid(), NodeVO.class);
        String zoneUuidA = tunnelBase.getZoneUuid(nodeA.getUuid());
        String zoneUuidZ = tunnelBase.getZoneUuid(nodeZ.getUuid());

        TunnelType tunnelType = tunnelBase.getTunnelType(nodeA, nodeZ, innerEndpointUuid);

        //专线费用
        if(tunnelType == TunnelType.CITY || tunnelType == TunnelType.REGION || tunnelType == TunnelType.LONG){
            ProductPriceUnit unit = getTunnelPriceUnitCN(bandwidthOfferingUuid, tunnelType, zoneUuidA);
            units.add(unit);
        }else if(tunnelType == TunnelType.ABROAD){
            ProductPriceUnit unit = getTunnelPriceUnitAb(bandwidthOfferingUuid, nodeA, nodeZ);
            units.add(unit);
        }else {
            EndpointVO innerEndpoint = dbf.findByUuid(innerEndpointUuid, EndpointVO.class);
            NodeVO nodeB = dbf.findByUuid(innerEndpoint.getNodeUuid(), NodeVO.class);

            if (nodeA.getCountry().equals("CHINA")) {
                TunnelType tunnelTypeAB = tunnelBase.getTunnelType(nodeA, nodeB, null);

                ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeZ);
                ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, tunnelTypeAB, zoneUuidA);

                units.add(unitInner);
                units.add(unitOuter);
            } else {
                TunnelType tunnelTypeBZ = tunnelBase.getTunnelType(nodeZ, nodeB, null);

                ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, tunnelTypeBZ, zoneUuidZ);
                ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeA);

                units.add(unitInner);
                units.add(unitOuter);
            }
        }

        //端口占用费
        if(isShareInterfaceUuidA){
            ProductPriceUnit unit = getTunnelPriceUintForSharePort(bandwidthOfferingUuid, nodeA, evoA);
            units.add(unit);
        }

        if(isShareInterfaceUuidZ){
            ProductPriceUnit unit = getTunnelPriceUintForSharePort(bandwidthOfferingUuid, nodeZ, evoZ);
            units.add(unit);
        }

        return units;
    }

    /**
     * 获取云专线单价--共享端口占用费
     */
    public ProductPriceUnit getTunnelPriceUintForSharePort(String bandwidthOfferingUuid,NodeVO nodeVO,EndpointVO endpointVO){
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setProductTypeCode(ProductType.PORT);
        unit.setCategoryCode(Category.SHARE);
        unit.setAreaCode(nodeVO.getUuid());
        unit.setLineCode(endpointVO.getUuid());
        unit.setConfigCode(getSharePortBandwidthOffering(bandwidthOfferingUuid));
        return unit;
    }

    /**
     * 通过专线带宽获取共享端口的带宽规格
     */
    public String getSharePortBandwidthOffering(String bandwidthOfferingUuid){
        final String offeringA = "LT500M";
        final String offeringB = "GT500MLT2G";
        final String offeringC = "GT2G";

        Long bandwidth = dbf.findByUuid(bandwidthOfferingUuid, BandwidthOfferingVO.class).getBandwidth();
        if(bandwidth < 524288000L){
            return offeringA;
        }else if(bandwidth > 2147483648L){
            return offeringC;
        }else{
            return offeringB;
        }
    }

    /**
     * 获取云专线单价--国内互传单价-同城/同区域/长传
     */
    public ProductPriceUnit getTunnelPriceUnitCN(String bandwidthOfferingUuid, TunnelType tunnelType, String zoneUuid) {
        ProductPriceUnit unit = new ProductPriceUnit();

        Category category;
        String areaCode;
        String lineCode;

        if(tunnelType == TunnelType.CITY){
            category = Category.CITY;
            areaCode = "DEFAULT";
            lineCode = "DEFAULT";
        }else if(tunnelType == TunnelType.REGION){
            category = Category.REGION;
            areaCode = zoneUuid;
            lineCode = "DEFAULT";
        }else{
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
        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, vo.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).findValue();
        data.add(new DescriptionItem("端口类型", portType));
        return JSONObjectUtil.toJsonString(data);
    }

    /**
     * 获取edgeLine Description
     */
    public String getDescriptionForEdgeLine(EdgeLineVO vo){
        DescriptionData data = new DescriptionData();
        data.add(new DescriptionItem("连接点", vo.getInterfaceVO().getEndpointVO().getName()));
        data.add(new DescriptionItem("物理接口", vo.getInterfaceVO().getName()));
        return JSONObjectUtil.toJsonString(data);
    }

    /**
     * 获取tunnel Description
     */
    public String getDescriptionForTunnel(TunnelVO vo,Long newBandwidth) {
        TunnelBase tunnelBase = new TunnelBase();
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

        data.add(new DescriptionItem("连接点-A", endpointNameA));
        data.add(new DescriptionItem("连接点-Z", endpointNameZ));

        if(tunnelBase.isShareForInterface(tunnelSwitchPortVOA.getInterfaceUuid())){
            data.add(new DescriptionItem("端口-A", "共享端口"));
        }else{
            data.add(new DescriptionItem("端口-A", "独享端口"));
        }

        if(tunnelBase.isShareForInterface(tunnelSwitchPortVOZ.getInterfaceUuid())){
            data.add(new DescriptionItem("端口-Z", "共享端口"));
        }else{
            data.add(new DescriptionItem("端口-Z", "独享端口"));
        }

        Long bandwidth;
        if(newBandwidth != null){
            bandwidth = newBandwidth;
        }else{
            bandwidth = vo.getBandwidth();
        }
        if(bandwidth < 1073741824L){
            data.add(new DescriptionItem("带宽", String.valueOf(SizeUnit.BYTE.toMegaByte(bandwidth))+"M"));
        }else{
            data.add(new DescriptionItem("带宽", String.valueOf(SizeUnit.BYTE.toGigaByte(bandwidth))+"G"));
        }

        return JSONObjectUtil.toJsonString(data);
    }

    /**
     * 手动创建Tunnel的订单
     */
    public ProductInfoForOrder createBuyOrderForTunnelManual(TunnelVO vo, APICreateTunnelManualMsg msg, NotifyCallBackData callBack) {
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
        order.setDescriptionData(getDescriptionForTunnel(vo,null));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getInterfaceAUuid(), msg.getInterfaceZUuid(), evoA, evoZ, msg.getInnerConnectedEndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    /**
     * 创建Tunnel的订单
     */
    public ProductInfoForOrder createBuyOrderForTunnel(TunnelVO vo, APICreateTunnelMsg msg, NotifyCallBackData callBack) {
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
        order.setDescriptionData(getDescriptionForTunnel(vo,null));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getInterfaceAUuid(), msg.getInterfaceZUuid(), evoA, evoZ, msg.getInnerConnectedEndpointUuid()));
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
