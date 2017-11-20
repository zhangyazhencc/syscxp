package com.syscxp.tunnel.solution.header;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionInterfaceVO.class)
public class SolutionInterfaceInventory {
    private String uuid;
    private String solutionUuid;
    private String name;
    private String description;
    private String cost;
    private String productChargeModel;
    private int duration;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    private String endpointName;
    private String portOfferingName;

    public static SolutionInterfaceInventory valueOf(SolutionInterfaceVO vo) {
        SolutionInterfaceInventory inv = new SolutionInterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setCost(vo.getCost());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setDuration(vo.getDuration());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        inv.setEndpointName(vo.getEndpointName());
        inv.setPortOfferingName(vo.getPortOfferingName());
        return inv;
    }

    public static List<SolutionInterfaceInventory> valueOf(Collection<SolutionInterfaceVO> vos) {
        List<SolutionInterfaceInventory> list = new ArrayList<>(vos.size());
        for (SolutionInterfaceVO vo : vos) {
            list.add(SolutionInterfaceInventory.valueOf(vo));
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

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getPortOfferingName() {
        return portOfferingName;
    }

    public void setPortOfferingName(String portOfferingName) {
        this.portOfferingName = portOfferingName;
    }
}
