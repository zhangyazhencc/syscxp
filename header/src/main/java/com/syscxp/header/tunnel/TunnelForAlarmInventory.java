package com.syscxp.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.tunnel.TunnelMonitorState;
import com.syscxp.header.tunnel.tunnel.TunnelState;
import com.syscxp.header.tunnel.tunnel.TunnelStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Inventory(mappingVOClass = TunnelForAlarmVO.class)
public class TunnelForAlarmInventory {

    private String uuid;
    private String accountUuid;
    private String ownerAccountUuid;
    private Integer vsi;
    private String monitorCidr;
    private String name;
    private Long bandwidth;
    private Double distance;
    private TunnelState state;
    private TunnelStatus status;
    private TunnelMonitorState monitorState;
    private Integer duration;
    private ProductChargeModel productChargeModel;
    private Integer maxModifies;
    private String description;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelForAlarmInventory valueOf(TunnelForAlarmVO vo){
        TunnelForAlarmInventory inv = new TunnelForAlarmInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inv.setVsi(vo.getVsi());
        inv.setMonitorCidr(vo.getMonitorCidr());
        inv.setName(vo.getName());
        inv.setBandwidth(vo.getBandwidth());
        inv.setDistance(vo.getDistance());
        inv.setState(vo.getState());
        inv.setStatus(vo.getStatus());
        inv.setMonitorState(vo.getMonitorState());
        inv.setDuration(vo.getDuration());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setDescription(vo.getDescription());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TunnelForAlarmInventory> valueOf(Collection<TunnelForAlarmVO> vos) {
        List<TunnelForAlarmInventory> lst = new ArrayList<TunnelForAlarmInventory>(vos.size());
        for (TunnelForAlarmVO vo : vos) {
            lst.add(TunnelForAlarmInventory.valueOf(vo));
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

    public TunnelState getState() {
        return state;
    }

    public void setState(TunnelState state) {
        this.state = state;
    }

    public TunnelStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelStatus status) {
        this.status = status;
    }

    public TunnelMonitorState getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(TunnelMonitorState monitorState) {
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

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
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

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }
}
