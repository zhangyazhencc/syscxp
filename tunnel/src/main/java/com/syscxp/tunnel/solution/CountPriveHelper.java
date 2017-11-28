package com.syscxp.tunnel.solution;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductPriceUnit;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO_;

import java.util.ArrayList;
import java.util.List;

public class CountPriveHelper {
    private DatabaseFacade dbf;

    public CountPriveHelper(DatabaseFacade dbf) {
        this.dbf = dbf;
    }


    public List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid, String nodeAUuid, String nodeZUuid, String innerEndpointUuid) {
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
}
