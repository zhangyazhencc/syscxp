package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.switchs.SwitchPortType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/10/30
 */
@Inventory(mappingVOClass = PortOfferingVO.class)
public class PortOfferingInventory {

    private String uuid;

    private String name;

    private SwitchPortType type;

    private String description;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static PortOfferingInventory valueOf(PortOfferingVO vo){
        PortOfferingInventory inv = new PortOfferingInventory();

        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setType(vo.getType());
        inv.setDescription(vo.getDescription());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<PortOfferingInventory> valueOf(Collection<PortOfferingVO> vos) {
        List<PortOfferingInventory> lst = new ArrayList<PortOfferingInventory>(vos.size());
        for (PortOfferingVO vo : vos) {
            lst.add(PortOfferingInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SwitchPortType getType() {
        return type;
    }

    public void setType(SwitchPortType type) {
        this.type = type;
    }
}
