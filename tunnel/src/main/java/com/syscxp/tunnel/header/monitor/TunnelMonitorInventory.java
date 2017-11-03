package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
@Inventory(mappingVOClass = TunnelMonitorVO.class)
public class TunnelMonitorInventory {

    private String uuid;

    private String tunnelUuid;

    private String monitorCidr;

    private String  tunnelSwitchPortUuid;

    private String  hostUuid;

    private String  monitorIp;

    private String msg;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static TunnelMonitorInventory valueOf(TunnelMonitorVO vo){
        TunnelMonitorInventory inventory = new TunnelMonitorInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setTunnelSwitchPortUuid(vo.getTunnelSwitchPortUuid());
        inventory.setHostUuid(vo.getHostUuid());
        inventory.setMonitorIp(vo.getMonitorIp());
        inventory.setMsg(vo.getMsg());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());
        return inventory;
    }

    public static List<TunnelMonitorInventory> valueOf(Collection<TunnelMonitorVO> vos){
        List<TunnelMonitorInventory> lst = new ArrayList<TunnelMonitorInventory>(vos.size());
        for(TunnelMonitorVO vo:vos){
            lst.add(TunnelMonitorInventory.valueOf(vo));
        }

        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getTunnelSwitchPortUuid() {
        return tunnelSwitchPortUuid;
    }

    public void setTunnelSwitchPortUuid(String tunnelSwitchPortUuid) {
        this.tunnelSwitchPortUuid = tunnelSwitchPortUuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }
}

