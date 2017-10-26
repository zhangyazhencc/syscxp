package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table
@Entity
public class ContactGroupRefVO extends BaseVO {

    @Column
    private String ContactUuid;

    @Column
    private String groupUuid;

    public String getContactUuid() {
        return ContactUuid;
    }

    public void setContactUuid(String contactUuid) {
        ContactUuid = contactUuid;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }
}
