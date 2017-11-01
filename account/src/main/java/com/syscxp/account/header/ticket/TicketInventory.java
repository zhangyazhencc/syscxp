package com.syscxp.account.header.ticket;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TicketVO.class)
public class TicketInventory {

    private String uuid;
    private String accountUuid;
    private String userUuid;
    private String AdminUserUuid;

    private String ticketTypeCode;
    private String phone;
    private String email;

    private String content;
    private String contentExtra;
    private String status;
    private String ticketFrom;

    private Timestamp createDate;
    private Timestamp lastOpDate;


    public static TicketInventory valueOf(TicketVO vo) {
        TicketInventory inv = new TicketInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setUserUuid(vo.getUserUuid());
        inv.setAdminUserUuid(vo.getAdminUserUuid());
        inv.setContentExtra(vo.getContentExtra());
        inv.setTicketFrom(vo.getTicketFrom().toString());
        inv.setTicketTypeCode(vo.getTicketTypeCode());
        inv.setContent(vo.getContent());
        inv.setStatus(vo.getStatus().toString());
        inv.setPhone(vo.getPhone());
        inv.setEmail(vo.getEmail());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<TicketInventory> valueOf(Collection<TicketVO> vos) {
        List<TicketInventory> lst = new ArrayList<TicketInventory>(vos.size());
        for (TicketVO vo : vos) {
            lst.add(TicketInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getTicketTypeCode() {
        return ticketTypeCode;
    }

    public void setTicketTypeCode(String ticketTypeCode) {
        this.ticketTypeCode = ticketTypeCode;
    }

    public String getContentExtra() {
        return contentExtra;
    }

    public void setContentExtra(String contentExtra) {
        this.contentExtra = contentExtra;
    }

    public String getTicketFrom() {
        return ticketFrom;
    }

    public void setTicketFrom(String ticketFrom) {
        this.ticketFrom = ticketFrom;
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

    public String getAdminUserUuid() {
        return AdminUserUuid;
    }

    public void setAdminUserUuid(String adminUserUuid) {
        AdminUserUuid = adminUserUuid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
