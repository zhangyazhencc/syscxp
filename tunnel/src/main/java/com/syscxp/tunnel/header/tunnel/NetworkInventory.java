package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-07
 */
@Inventory(mappingVOClass = NetworkVO.class)
public class NetworkInventory {

    private String uuid;
    private String accountUuid;
    private String name;
    private Integer vsi;
    private String monitorCidr;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static NetworkInventory valueOf(NetworkVO vo){
        NetworkInventory inv = new NetworkInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setVsi(vo.getVsi());
        inv.setMonitorCidr(vo.getMonitorCidr());
        inv.setDescription(vo.getDescription());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<NetworkInventory> valueOf(Collection<NetworkVO> vos) {
        List<NetworkInventory> lst = new ArrayList<NetworkInventory>(vos.size());
        for (NetworkVO vo : vos) {
            lst.add(NetworkInventory.valueOf(vo));
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

    public Integer getVsi() {
        return vsi;
    }

    public void setVsi(Integer vsi) {
        this.vsi = vsi;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}