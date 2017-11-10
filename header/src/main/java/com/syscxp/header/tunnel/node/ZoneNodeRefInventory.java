package com.syscxp.header.tunnel.node;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/11/1
 */
@Inventory(mappingVOClass = ZoneNodeRefVO.class)
public class ZoneNodeRefInventory {
    private String uuid;
    private String nodeUuid;
    private String zoneUuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static ZoneNodeRefInventory valueOf(ZoneNodeRefVO vo){
        ZoneNodeRefInventory inv = new ZoneNodeRefInventory();

        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setZoneUuid(vo.getZoneUuid());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<ZoneNodeRefInventory> valueOf(Collection<ZoneNodeRefVO> vos) {
        List<ZoneNodeRefInventory> lst = new ArrayList<ZoneNodeRefInventory>(vos.size());
        for (ZoneNodeRefVO vo : vos) {
            lst.add(ZoneNodeRefInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
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
