package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class ContactVO extends BaseVO{

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String mobile;

    @Column
    private String accountUuid;

    @OneToMany(fetch =FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name="ContactNotifyWayRefVO",
            joinColumns={@JoinColumn(name="contactUuid",referencedColumnName="uuid", insertable = false, updatable = false)},
            inverseJoinColumns={@JoinColumn(name="notifyWayUuid",referencedColumnName="uuid", insertable = false, updatable = false)}
    )
    private Set<NotifyWayVO> notifyWayVOs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Set<NotifyWayVO> getNotifyWayVOs() {
        return notifyWayVOs;
    }

    public void setNotifyWayVOs(Set<NotifyWayVO> notifyWayVOs) {
        this.notifyWayVOs = notifyWayVOs;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
