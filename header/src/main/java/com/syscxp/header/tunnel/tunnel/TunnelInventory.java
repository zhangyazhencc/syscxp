package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by DCY on 2017-09-11
 */
@Inventory(mappingVOClass = TunnelVO.class)
public class TunnelInventory {

    private String uuid;
    private String accountUuid;
    private String ownerAccountUuid;
    private Integer vsi;
    private String monitorCidr;
    private List<TunnelSwitchPortInventory> tunnelSwitchs = new ArrayList<TunnelSwitchPortInventory>();
    private String name;
    private Long bandwidth;
    private Double distance;
    private String state;
    private String status;
    private String monitorState;
    private Integer duration;
    private String productChargeModel;
    private Integer maxModifies;
    private String description;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelInventory valueOf(TunnelVO vo){
        TunnelInventory inv = new TunnelInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inv.setVsi(vo.getVsi());
        inv.setMonitorCidr(vo.getMonitorCidr());
        inv.setTunnelSwitchs(TunnelSwitchPortInventory.valueOf(vo.getTunnelSwitchPortVOS()));
        inv.setName(vo.getName());
        inv.setBandwidth(vo.getBandwidth());
        inv.setDistance(vo.getDistance());
        inv.setState(vo.getState().toString());
        inv.setStatus(vo.getStatus().toString());
        inv.setMonitorState(vo.getMonitorState().toString());
        inv.setDuration(vo.getDuration());
        inv.setProductChargeModel(vo.getProductChargeModel().toString());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setDescription(vo.getDescription());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TunnelInventory> valueOf(Collection<TunnelVO> vos) {
        List<TunnelInventory> lst = new ArrayList<TunnelInventory>(vos.size());
        for (TunnelVO vo : vos) {
            lst.add(TunnelInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(String monitorState) {
        this.monitorState = monitorState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public Integer getVsi() {
        return vsi;
    }

    public void setVsi(Integer vsi) {
        this.vsi = vsi;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public List<TunnelSwitchPortInventory> getTunnelSwitchs() {
        return tunnelSwitchs;
    }

    public void setTunnelSwitchs(List<TunnelSwitchPortInventory> tunnelSwitchs) {
        this.tunnelSwitchs = tunnelSwitchs;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }
}
