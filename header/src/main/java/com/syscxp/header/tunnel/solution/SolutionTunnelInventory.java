package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionTunnelVO.class)
public class SolutionTunnelInventory {
    private String uuid;
    private String solutionUuid;
    private String name;
    private String description;
    private String cost;
    private ProductChargeModel productChargeModel;
    private int duration;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    private String endpointUuidA;
    private String endpointUuidZ;
    private Long bandwidth;

    public static SolutionTunnelInventory valueOf(SolutionTunnelVO vo) {
        SolutionTunnelInventory inv = new SolutionTunnelInventory();
        inv.setUuid(vo.getUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setCost(vo.getCost());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setDuration(vo.getDuration());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        inv.setEndpointUuidA(vo.getEndpointUuidA());
        inv.setEndpointUuidZ(vo.getEndpointUuidZ());
        inv.setBandwidth(vo.getBandwidth());

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

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
