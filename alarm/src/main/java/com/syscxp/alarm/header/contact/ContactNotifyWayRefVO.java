package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class ContactNotifyWayRefVO extends BaseVO{

    @Column
    private String contactUuid;

    @Column
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
}
