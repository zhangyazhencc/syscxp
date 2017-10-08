package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/9/29
 */
@Inventory(mappingVOClass = PhysicalSwitchUpLinkRefVO.class)
public class PhysicalSwitchUpLinkRefInventory {

    private String uuid;
    private String physicalSwitchUuid;
    private String portName;
    private String uplinkPhysicalSwitchUuid;
    private String uplinkPhysicalSwitchPortName;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static PhysicalSwitchUpLinkRefInventory valueOf(PhysicalSwitchUpLinkRefVO vo){
        PhysicalSwitchUpLinkRefInventory inv = new PhysicalSwitchUpLinkRefInventory();

        inv.setUuid(vo.getUuid());
        inv.setPhysicalSwitchUuid(vo.getPhysicalSwitchUuid());
        inv.setPortName(vo.getPortName());
        inv.setUplinkPhysicalSwitchUuid(vo.getUplinkPhysicalSwitchUuid());
        inv.setUplinkPhysicalSwitchPortName(vo.getUplinkPhysicalSwitchPortName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<PhysicalSwitchUpLinkRefInventory> valueOf(Collection<PhysicalSwitchUpLinkRefVO> vos) {
        List<PhysicalSwitchUpLinkRefInventory> lst = new ArrayList<PhysicalSwitchUpLinkRefInventory>(vos.size());
        for (PhysicalSwitchUpLinkRefVO vo : vos) {
            lst.add(PhysicalSwitchUpLinkRefInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getUplinkPhysicalSwitchUuid() {
        return uplinkPhysicalSwitchUuid;
    }

    public void setUplinkPhysicalSwitchUuid(String uplinkPhysicalSwitchUuid) {
        this.uplinkPhysicalSwitchUuid = uplinkPhysicalSwitchUuid;
    }

    public String getUplinkPhysicalSwitchPortName() {
        return uplinkPhysicalSwitchPortName;
    }

    public void setUplinkPhysicalSwitchPortName(String uplinkPhysicalSwitchPortName) {
        this.uplinkPhysicalSwitchPortName = uplinkPhysicalSwitchPortName;
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
