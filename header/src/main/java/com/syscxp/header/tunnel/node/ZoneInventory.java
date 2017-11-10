package com.syscxp.header.tunnel.node;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/11/1
 */
@Inventory(mappingVOClass = ZoneVO.class)
public class ZoneInventory {
    private String uuid;
    private String name;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static ZoneInventory valueOf(ZoneVO vo){
        ZoneInventory inv = new ZoneInventory();

        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<ZoneInventory> valueOf(Collection<ZoneVO> vos) {
        List<ZoneInventory> lst = new ArrayList<ZoneInventory>(vos.size());
        for (ZoneVO vo : vos) {
            lst.add(ZoneInventory.valueOf(vo));
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
