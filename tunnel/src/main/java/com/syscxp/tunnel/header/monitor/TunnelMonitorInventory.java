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

    private String accountUuid;

    private String msg;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    private List<TunnelMonitorInterfaceInventory> tunnelMonitorInterfaceInventories;

    public static TunnelMonitorInventory valueOf(TunnelMonitorVO vo){
        TunnelMonitorInventory inventory = new TunnelMonitorInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setMsg(vo.getMsg());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());
        inventory.setTunnelMonitorInterfaceInventories(TunnelMonitorInterfaceInventory.valueOf(vo.getTunnelMonitorInterfaceVOList()));

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

    public List<TunnelMonitorInterfaceInventory> getTunnelMonitorInterfaceInventories() {
        return tunnelMonitorInterfaceInventories;
    }

    public void setTunnelMonitorInterfaceInventories(List<TunnelMonitorInterfaceInventory> tunnelMonitorInterfaceInventories) {
        this.tunnelMonitorInterfaceInventories = tunnelMonitorInterfaceInventories;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}

