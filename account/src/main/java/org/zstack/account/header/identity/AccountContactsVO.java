package org.zstack.account.header.identity;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/18.
 */

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AccountContactsVO {
    @Id
    @Column
    private String uuid;
    
    @Column
    private String accountUuid;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private NoticeWay noticeWay;

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

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public NoticeWay getNoticeWay() {
        return noticeWay;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNoticeWay(NoticeWay noticeWay) {
        this.noticeWay = noticeWay;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
