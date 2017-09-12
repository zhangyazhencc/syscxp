package org.zstack.account.header.account;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AccountExtraInfoVO {
    @Id
    @Column
    private String uuid;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountGrade grade;

    @Column
    private String userUuid;

    @Column
    private String createWay;

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

    public AccountGrade getGrade() {
        return grade;
    }

    public String getUserUuid() {
        return userUuid;
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

    public void setGrade(AccountGrade grade) {
        this.grade = grade;
    }

    public void setUserUuid(String salesman) {
        this.userUuid = salesman;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public void setCreateWay(String createWay) {
        this.createWay = createWay;
    }

    public String getCreateWay() {

        return createWay;
    }

}
