package com.syscxp.account.header.log;

import com.syscxp.core.db.converter.ListAttributeConverter;
import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AlarmContactVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    @Convert(converter = ListAttributeConverter.class)
    private List<AlarmChannel> channel;

    @Column
    private String accountName;

    @Column
    private String company;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public List<AlarmChannel> getChannel() {
        return channel;
    }

    public void setChannel(List<AlarmChannel> channel) {
        this.channel = channel;
    }
}
