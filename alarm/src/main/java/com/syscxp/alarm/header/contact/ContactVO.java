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

    @OneToMany(fetch =FetchType.EAGER)
    @JoinTable(name="ContactNotifyWayRefVO",
            joinColumns={@JoinColumn(name="contactUuid",referencedColumnName="uuid")},
            inverseJoinColumns={@JoinColumn(name="notifyWayUuid",referencedColumnName="uuid")}
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
}