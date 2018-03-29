package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/3/28
 */
@Inventory(mappingVOClass = OutsideResourceVO.class)
public class OutsideResourceInventory {

    private String uuid;
    private String resourceType;
    private String resourceUuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static OutsideResourceInventory valueOf(OutsideResourceVO vo){
        OutsideResourceInventory inv = new OutsideResourceInventory();
        inv.setUuid(vo.getUuid());
        inv.setResourceType(vo.getResourceType());
        inv.setResourceUuid(vo.getResourceUuid());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<OutsideResourceInventory> valueOf(Collection<OutsideResourceVO> vos) {
        List<OutsideResourceInventory> lst = new ArrayList<OutsideResourceInventory>(vos.size());
        for (OutsideResourceVO vo : vos) {
            lst.add(OutsideResourceInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
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
