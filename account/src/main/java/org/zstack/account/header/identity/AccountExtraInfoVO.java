package org.zstack.account.header.identity;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.*;
import org.zstack.header.vo.Index;

import javax.persistence.*;
import javax.persistence.ForeignKey;
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
    @Index
    private String accountUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountGrade grade;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = UserVO.class, parentKey = "uuid", onDeleteAction = org.zstack.header.vo.ForeignKey.ReferenceOption.CASCADE)
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

    public String getAccountUuid() {
        return accountUuid;
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

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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
