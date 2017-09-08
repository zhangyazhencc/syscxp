package org.zstack.tunnel.header.tunnel;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-07
 */
@Inventory(mappingVOClass = NetWorkVO.class)
public class NetWorkInventory {

    private String uuid;
    private String accountUuid;
    private String name;
    private Integer vsi;
    private String monitorIp;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static NetWorkInventory valueOf(NetWorkVO vo){
        NetWorkInventory inv = new NetWorkInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setVsi(vo.getVsi());
        inv.setMonitorIp(vo.getMonitorIp());
        inv.setDescription(vo.getDescription());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<NetWorkInventory> valueOf(Collection<NetWorkVO> vos) {
        List<NetWorkInventory> lst = new ArrayList<NetWorkInventory>(vos.size());
        for (NetWorkVO vo : vos) {
            lst.add(NetWorkInventory.valueOf(vo));
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

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
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
