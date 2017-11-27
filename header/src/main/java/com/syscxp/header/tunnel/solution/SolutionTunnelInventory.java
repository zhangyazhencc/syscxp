package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionTunnelVO.class)
public class SolutionTunnelInventory {
    private String uuid;
    private String solutionUuid;
    private BigDecimal cost;
    private ProductChargeModel productChargeModel;
    private int duration;
    private Timestamp lastOpDate;
    private Timestamp createDate;
    private String bandwidthOfferingUuid;
    private String endpointNameA;
    private String endpointNameZ;

    public static SolutionTunnelInventory valueOf(SolutionTunnelVO vo) {
        SolutionTunnelInventory inv = new SolutionTunnelInventory();
        inv.setUuid(vo.getUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setCost(vo.getCost());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setDuration(vo.getDuration());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        if(vo.getEndpointVOA() != null){
            inv.setEndpointNameA(vo.getEndpointVOA().getName());
        }
        if(vo.getEndpointVOZ() != null){
            inv.setEndpointNameZ(vo.getEndpointVOZ().getName());
        }
        inv.setBandwidthOfferingUuid(vo.getBandwidthOfferingUuid());

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

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
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

    public String getEndpointNameA() {
        return endpointNameA;
    }

    public void setEndpointNameA(String endpointNameA) {
        this.endpointNameA = endpointNameA;
    }

    public String getEndpointNameZ() {
        return endpointNameZ;
    }

    public void setEndpointNameZ(String endpointNameZ) {
        this.endpointNameZ = endpointNameZ;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }
}
