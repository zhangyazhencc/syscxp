package org.zstack.account.header.ticket;

import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/9/25.
 */

@Entity
@Table
public class TicketRecordVO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = TicketVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String ticketUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private RecordBy recordBy;

    @Column
    private String adminUserUuid;

    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

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

    public String getTicketUuid() {
        return ticketUuid;
    }

    public void setTicketUuid(String ticketUuid) {
        this.ticketUuid = ticketUuid;
    }

    public RecordBy getRecordBy() {
        return recordBy;
    }

    public void setRecordBy(RecordBy belongTo) {
        this.recordBy = belongTo;
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

    public String getAdminUserUuid() {
        return adminUserUuid;
    }

    public void setAdminUserUuid(String adminUserUuid) {
        this.adminUserUuid = adminUserUuid;
    }
}
