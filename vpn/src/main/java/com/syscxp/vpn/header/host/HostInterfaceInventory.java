package com.syscxp.vpn.header.host;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = HostInterfaceVO.class)
public class HostInterfaceInventory {
    private String uuid;
    private String interfaceName;
    private String hostUuid;
    private String endpointUuid;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostInterfaceInventory valueOf(HostInterfaceVO vo){
        HostInterfaceInventory inv = new HostInterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setInterfaceName(vo.getInterfaceName());
        inv.setHostUuid(vo.getHostUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<HostInterfaceInventory> valueOf(Collection<HostInterfaceVO> vos){
        List<HostInterfaceInventory> invs = new ArrayList<HostInterfaceInventory>(vos.size());
        for (HostInterfaceVO vo:vos){
            invs.add(HostInterfaceInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
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
