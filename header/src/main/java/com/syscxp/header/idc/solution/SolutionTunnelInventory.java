package com.syscxp.header.idc.solution;

import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionTunnelVO.class)
public class SolutionTunnelInventory {
    private String uuid;
    private String name;
    private String solutionUuid;

    private String bandwidthOfferingUuid;
    private String endpointUuidA;
    private String endpointUuidZ;
    private String innerEndpointUuid;
    private String interfaceUuidA;
    private String interfaceUuidZ;
    private String type;

    private BigDecimal cost;
    private BigDecimal discount;
    private BigDecimal shareDiscount;
    private String productChargeModel;
    private int duration;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SolutionTunnelInventory valueOf(SolutionTunnelVO vo) {
        SolutionTunnelInventory inv = new SolutionTunnelInventory();
        inv.setUuid(vo.getUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setCost(vo.getCost());
        inv.setDiscount(vo.getDiscount());
        inv.setShareDiscount(vo.getShareDiscount());
        inv.setProductChargeModel(vo.getProductChargeModel().toString());
        inv.setDuration(vo.getDuration());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setName(vo.getName());
        inv.setEndpointUuidA(vo.getEndpointUuidA());
        inv.setEndpointUuidZ(vo.getEndpointUuidZ());
        inv.setInnerEndpointUuid(vo.getInnerEndpointUuid());

        inv.setInterfaceUuidA(vo.getInterfaceUuidA());
        inv.setInterfaceUuidZ(vo.getInterfaceUuidZ());
        inv.setBandwidthOfferingUuid(vo.getBandwidthOfferingUuid());
        inv.setType(vo.getType().toString());

        return inv;
    }

    public static List<SolutionTunnelInventory> valueOf(Collection<SolutionTunnelVO> vos) {
        List<SolutionTunnelInventory> list = new ArrayList<>(vos.size());
        for (SolutionTunnelVO vo : vos) {
            list.add(SolutionTunnelInventory.valueOf(vo));
        }
        return list;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getEndpointUuidA() {
        return endpointUuidA;
    }

    public void setEndpointUuidA(String endpointUuidA) {
        this.endpointUuidA = endpointUuidA;
    }

    public String getEndpointUuidZ() {
        return endpointUuidZ;
    }

    public void setEndpointUuidZ(String endpointUuidZ) {
        this.endpointUuidZ = endpointUuidZ;
    }

    public String getInnerEndpointUuid() {
        return innerEndpointUuid;
    }

    public void setInnerEndpointUuid(String innerEndpointUuid) {
        this.innerEndpointUuid = innerEndpointUuid;
    }

    public String getInterfaceUuidA() {
        return interfaceUuidA;
    }

    public void setInterfaceUuidA(String interfaceUuidA) {
        this.interfaceUuidA = interfaceUuidA;
    }

    public String getInterfaceUuidZ() {
        return interfaceUuidZ;
    }

    public void setInterfaceUuidZ(String interfaceUuidZ) {
        this.interfaceUuidZ = interfaceUuidZ;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShareDiscount() {
        return shareDiscount;
    }

    public void setShareDiscount(BigDecimal shareDiscount) {
        this.shareDiscount = shareDiscount;
    }
}
