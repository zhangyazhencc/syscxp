package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
@Inventory(mappingVOClass = L3NetworkMonitorVO.class)
public class L3NetworkMonitorInventory {

    private String uuid;

    private String l3NetworkUuid;

    private String l3EndPointUuid;

    private String hostUuid;

    private String monitorIp;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static L3NetworkMonitorInventory valueOf(L3NetworkMonitorVO vo){
        L3NetworkMonitorInventory inventory = new L3NetworkMonitorInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setL3NetworkUuid(vo.getL3NetworkUuid());
        inventory.setL3EndPointUuid(vo.getL3EndPointUuid());
        inventory.setHostUuid(vo.getHostUuid());
        inventory.setMonitorIp(vo.getMonitorIp());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());
        return inventory;
    }

    public static List<L3NetworkMonitorInventory> valueOf(Collection<L3NetworkMonitorVO> vos){
        List<L3NetworkMonitorInventory> lst = new ArrayList<L3NetworkMonitorInventory>(vos.size());
        for(L3NetworkMonitorVO vo:vos){
            lst.add(L3NetworkMonitorInventory.valueOf(vo));
        }

        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getL3EndPointUuid() {
        return l3EndPointUuid;
    }

    public void setL3EndPointUuid(String l3EndPointUuid) {
        this.l3EndPointUuid = l3EndPointUuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
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

