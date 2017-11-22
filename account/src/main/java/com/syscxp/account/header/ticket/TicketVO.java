package com.syscxp.account.header.ticket;

import com.syscxp.account.header.account.AccountVO;
import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by wangwg on 2017/9/25.
 */

@Entity
@Table
public class TicketVO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = AccountVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String accountUuid;

    @Column
    private String userUuid;

    @Column
    private String adminUserUuid;

    @Column
    private String ticketTypeUuid;

    @Column
    private String content;

    @Column
    private String contentExtra;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private TicketFrom ticketFrom;

    @Column
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @OneToOne(cascade = CascadeType.REFRESH,fetch=FetchType.EAGER)
    @JoinColumn(name ="ticketTypeUuid", insertable = false,updatable = false)
    private TicketTypeVO ticketType;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
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


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
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

    public String getAdminUserUuid() {
        return adminUserUuid;
    }

    public void setAdminUserUuid(String adminUserUuid) {
        this.adminUserUuid = adminUserUuid;
    }

    public String getContentExtra() {
        return contentExtra;
    }

    public void setContentExtra(String contentExtra) {
        this.contentExtra = contentExtra;
    }

    public TicketFrom getTicketFrom() {
        return ticketFrom;
    }

    public void setTicketFrom(TicketFrom ticketFrom) {
        this.ticketFrom = ticketFrom;
    }

    public String getTicketTypeUuid() {
        return ticketTypeUuid;
    }

    public void setTicketTypeUuid(String ticketTypeUuid) {
        this.ticketTypeUuid = ticketTypeUuid;
    }

    public TicketTypeVO getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketTypeVO ticketType) {
        this.ticketType = ticketType;
    }
}
