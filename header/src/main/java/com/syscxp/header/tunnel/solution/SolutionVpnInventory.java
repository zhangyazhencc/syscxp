package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionVpnVO.class)
public class SolutionVpnInventory {
    private String uuid;
    private String solutionUuid;
    private String name;
    private String description;
    private String cost;
    private ProductChargeModel productChargeModel;
    private int duration;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    private String zoneUuid;
    private String endpointName;
    private long bandwidth;

    public static SolutionVpnInventory valueOf(SolutionVpnVO vo) {
        SolutionVpnInventory inv = new SolutionVpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setCost(vo.getCost());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setDuration(vo.getDuration());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        if(vo.getEndpointVO() != null){
            inv.setEndpointName(vo.getEndpointVO().getName());
        }
        inv.setZoneUuid(vo.getZoneUuid());
        inv.setBandwidth(vo.getBandwidth());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
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

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public long getBandwidth() {
        return bandwidth;
    }
}
