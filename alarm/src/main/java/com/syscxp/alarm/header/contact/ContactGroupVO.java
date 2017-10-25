package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Table
@Entity
public class ContactGroupVO extends BaseVO {

    @Column
    private String groupCode;

    @Column
    private String groupName;

    public String getGroupCode() {
        return groupCode;
    }

    @OneToMany(fetch =FetchType.EAGER)
    @JoinTable(name="ContactGroupRefVO",
            joinColumns={@JoinColumn(name="contactUuid",referencedColumnName="uuid")},
            inverseJoinColumns={@JoinColumn(name="groupUuid",referencedColumnName="uuid")}
    )
    private Set<ContactVO> contactVOList;

    public Set<ContactVO> getContactVOList() {
        return contactVOList;
    }

    public void setContactVOList(Set<ContactVO> contactVOList) {
        this.contactVOList = contactVOList;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
