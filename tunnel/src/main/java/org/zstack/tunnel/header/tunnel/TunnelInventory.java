package org.zstack.tunnel.header.tunnel;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Inventory(mappingVOClass = TunnelVO.class)
public class TunnelInventory {

    private String uuid;
    private String accountUuid;
    private String networkUuid;
    private NetworkInventory network;
    private String name;
    private Long bandwidth;
    private Double distance;
    private TunnelState state;
    private TunnelStatus status;
    private TunnelMonitorState monitorState;
    private Integer months;
    private String description;
    private Timestamp expiredDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelInventory valueOf(TunnelVO vo){
        TunnelInventory inv = new TunnelInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setNetworkUuid(vo.getNetworkUuid());
        inv.setNetwork(NetworkInventory.valueOf(vo.getNetworkVO()));
        inv.setName(vo.getName());
        inv.setBandwidth(vo.getBandwidth());
        inv.setDistance(vo.getDistance());
        inv.setState(vo.getState());
        inv.setStatus(vo.getStatus());
        inv.setMonitorState(vo.getMonitorState());
        inv.setMonths(vo.getMonths());
        inv.setDescription(vo.getDescription());
        inv.setExpiredDate(vo.getExpiredDate());
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

    public String getNetworkUuid() {
        return networkUuid;
    }

    public void setNetworkUuid(String networkUuid) {
        this.networkUuid = networkUuid;
    }

    public NetworkInventory getNetwork() {
        return network;
    }

    public void setNetwork(NetworkInventory network) {
        this.network = network;
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

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
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
}
