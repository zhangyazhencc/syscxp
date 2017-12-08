package com.syscxp.account.header.ticket;

import com.syscxp.header.search.Inventory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TicketRecordVO.class)
public class TicketRecordInventory {

    private String uuid;
    private String ticketUuid;
    private String recordBy;
    private String accountUuid;
    private String userUuid;

    private String content;
    private String status;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static TicketRecordInventory valueOf(TicketRecordVO vo) {
        TicketRecordInventory inv = new TicketRecordInventory();
        inv.setUuid(vo.getUuid());
        inv.setTicketUuid(vo.getTicketUuid());
        inv.setRecordBy(vo.getRecordBy().toString());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setUserUuid(vo.getUserUuid());
        inv.setContent(vo.getContent());
        inv.setStatus(vo.getStatus().toString());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<TicketRecordInventory> valueOf(Collection<TicketRecordVO> vos) {
        List<TicketRecordInventory> lst = new ArrayList<TicketRecordInventory>(vos.size());
        for (TicketRecordVO vo : vos) {
            lst.add(TicketRecordInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTicketUuid() {
        return ticketUuid;
    }

    public void setTicketUuid(String ticketUuid) {
        this.ticketUuid = ticketUuid;
    }

    public String getRecordBy() {
        return recordBy;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public void setRecordBy(String recordBy) {
        this.recordBy = recordBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
