package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class ContactNotifyWayRefVO {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @ForeignKey(parentEntityClass = ContactVO.class, onDeleteAction = ReferenceOption.CASCADE)
    private String contactUuid;

    @Column
    @ForeignKey(parentEntityClass = NotifyWayVO.class, onDeleteAction = ReferenceOption.CASCADE)
    private String notifyWayUuid;

    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
    }

    public String getNotifyWayUuid() {
        return notifyWayUuid;
    }

    public void setNotifyWayUuid(String notifyWayUuid) {
        this.notifyWayUuid = notifyWayUuid;
    }

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

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

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
