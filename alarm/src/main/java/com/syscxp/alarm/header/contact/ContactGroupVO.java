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

    @Column
    private String accountUuid;

    public String getGroupCode() {
        return groupCode;
    }

    @OneToMany(fetch =FetchType.EAGER)
    @JoinTable(name="ContactGroupRefVO",
            joinColumns={@JoinColumn(name="groupUuid",referencedColumnName="uuid")},
            inverseJoinColumns={@JoinColumn(name="contactUuid",referencedColumnName="uuid")}
    )
    private Set<ContactVO> contactVOList;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

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
