package com.syscxp.account.header.ticket;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TicketTypeVO.class)
public class TicketTypeInventory {

    private String uuid;
    private String name;
    private String category;

    private Timestamp createDate;
    private Timestamp lastOpDate;


    public static TicketTypeInventory valueOf(TicketTypeVO vo) {
        TicketTypeInventory inv = new TicketTypeInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setCategory(vo.getCategory());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<TicketTypeInventory> valueOf(Collection<TicketTypeVO> vos) {
        List<TicketTypeInventory> lst = new ArrayList<TicketTypeInventory>(vos.size());
        for (TicketTypeVO vo : vos) {
            lst.add(TicketTypeInventory.valueOf(vo));
        }
        return lst;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
