package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-20
 */
@Inventory(mappingVOClass = TunnelInterfaceVO.class)
public class TunnelInterfaceInventory {

    private String uuid;
    private String tunnelUuid;
    private String interfaceUuid;
    private InterfaceInventory interfaces;
    private Integer vlan;
    private String sortTag;
    private TunnelQinqState qinqState;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static TunnelInterfaceInventory valueOf(TunnelInterfaceVO vo){
        TunnelInterfaceInventory inv = new TunnelInterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setInterfaceUuid(vo.getInterfaceUuid());
        inv.setInterfaces(InterfaceInventory.valueOf(vo.getInterfaceVO()));
        inv.setVlan(vo.getVlan());
        inv.setSortTag(vo.getSortTag());
        inv.setQinqState(vo.getQinqState());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TunnelInterfaceInventory> valueOf(Collection<TunnelInterfaceVO> vos) {
        List<TunnelInterfaceInventory> lst = new ArrayList<TunnelInterfaceInventory>(vos.size());
        for (TunnelInterfaceVO vo : vos) {
            lst.add(TunnelInterfaceInventory.valueOf(vo));
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

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public InterfaceInventory getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(InterfaceInventory interfaces) {
        this.interfaces = interfaces;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getSortTag() {
        return sortTag;
    }

    public void setSortTag(String sortTag) {
        this.sortTag = sortTag;
    }

    public TunnelQinqState getQinqState() {
        return qinqState;
    }

    public void setQinqState(TunnelQinqState qinqState) {
        this.qinqState = qinqState;
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
