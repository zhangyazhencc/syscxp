package org.zstack.account.header.ticket;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TicketTypeVO.class)
public class TicketTypeInventory {

    private long id;
    private String typeValue;
    private String typeName;
    private Timestamp createDate;
    private Timestamp lastOpDate;


    public static TicketTypeInventory valueOf(TicketTypeVO vo) {
        TicketTypeInventory inv = new TicketTypeInventory();
        inv.setId(vo.getId());
        inv.setTypeName(vo.getTypeName());
        inv.setTypeValue(vo.getTypeValue());

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
