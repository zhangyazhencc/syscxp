package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@Inventory(mappingVOClass = HostSwitchMonitorVO.class)
public class HostSwitchMonitorInventory {
    private String uuid;

    private String hostUuid;

    private String physicalSwitchUuid;

    private String physicalSwitchPortName;

    private String interfaceName;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static HostSwitchMonitorInventory valueOf(HostSwitchMonitorVO vo){
        HostSwitchMonitorInventory inventory = new HostSwitchMonitorInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setHostUuid(vo.getHostUuid());
        inventory.setPhysicalSwitchUuid(vo.getPhysicalSwitchUuid());
        inventory.setPhysicalSwitchPortName(vo.getPhysicalSwitchPortName());
        inventory.setInterfaceName(vo.getInterfaceName());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());

        return  inventory;
    }

    public static List<HostSwitchMonitorInventory> valueOf(Collection<HostSwitchMonitorVO> vos) {
        List<HostSwitchMonitorInventory> lst = new ArrayList<HostSwitchMonitorInventory>(vos.size());
        for (HostSwitchMonitorVO vo : vos) {
            lst.add(HostSwitchMonitorInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getPhysicalSwitchPortName() {
        return physicalSwitchPortName;
    }

    public void setPhysicalSwitchPortName(String physicalSwitchPortName) {
        this.physicalSwitchPortName = physicalSwitchPortName;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
