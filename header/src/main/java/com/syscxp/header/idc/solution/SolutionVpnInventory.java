package com.syscxp.header.idc.solution;

import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionVpnVO.class)
public class SolutionVpnInventory {
    private String uuid;
    private String solutionUuid;
    private String name;

    private String solutionTunnelUuid;
    private String solutionTunnelName;
    private String endpointUuid;
    private String bandwidthOfferingUuid;

    private BigDecimal cost;
    private BigDecimal discount;
    private BigDecimal shareDiscount;
    private String productChargeModel;
    private int duration;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SolutionVpnInventory valueOf(SolutionVpnVO vo) {
        SolutionVpnInventory inv = new SolutionVpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setCost(vo.getCost());
        inv.setProductChargeModel(vo.getProductChargeModel().toString());
        inv.setDuration(vo.getDuration());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setName(vo.getName());
        inv.setDiscount(vo.getDiscount());
        inv.setShareDiscount(vo.getShareDiscount());
        if(vo.getSolutionTunnelVO() != null){
            inv.setSolutionTunnelName(vo.getSolutionTunnelVO().getName());
        }

        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setSolutionTunnelUuid(vo.getSolutionTunnelUuid());
        inv.setBandwidthOfferingUuid(vo.getBandwidthOfferingUuid());
        return inv;
    }

    public static List<SolutionVpnInventory> valueOf(Collection<SolutionVpnVO> vos) {
        List<SolutionVpnInventory> list = new ArrayList<>(vos.size());
        for (SolutionVpnVO vo : vos) {
            list.add(SolutionVpnInventory.valueOf(vo));
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

    public String getSolutionTunnelUuid() {
        return solutionTunnelUuid;
    }

    public void setSolutionTunnelUuid(String solutionTunnelUuid) {
        this.solutionTunnelUuid = solutionTunnelUuid;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSolutionTunnelName() {
        return solutionTunnelName;
    }

    public void setSolutionTunnelName(String solutionTunnelName) {
        this.solutionTunnelName = solutionTunnelName;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
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
