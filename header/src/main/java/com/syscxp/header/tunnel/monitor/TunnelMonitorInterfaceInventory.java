package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-25.
 * @Description: .
 */

@Inventory(mappingVOClass = TunnelMonitorInterfaceVO.class)
public class TunnelMonitorInterfaceInventory {
    private String uuid;

    private String tunnelMonitorUuid;

    private String interfaceUuid;

    private InterfaceType interfaceType;

    private String hostUuid;

    private String monitorIp;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static TunnelMonitorInterfaceInventory valueOf(TunnelMonitorInterfaceVO vo){
        TunnelMonitorInterfaceInventory inv = new TunnelMonitorInterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelMonitorUuid(vo.getTunnelMonitorUuid());
        inv.setInterfaceType(vo.getInterfaceType());
        inv.setHostUuid(vo.getHostUuid());
        inv.setMonitorIp(vo.getMonitorIp());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<TunnelMonitorInterfaceInventory> valueOf(Collection<TunnelMonitorInterfaceVO> vos){
        List<TunnelMonitorInterfaceInventory> lst = new ArrayList<TunnelMonitorInterfaceInventory>(vos.size());
        for(TunnelMonitorInterfaceVO vo:vos){
            lst.add(TunnelMonitorInterfaceInventory.valueOf(vo));
        }

        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelMonitorUuid() {
        return tunnelMonitorUuid;
    }

    public void setTunnelMonitorUuid(String tunnelMonitorUuid) {
        this.tunnelMonitorUuid = tunnelMonitorUuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
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
