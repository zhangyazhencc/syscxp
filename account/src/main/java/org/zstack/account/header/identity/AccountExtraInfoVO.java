package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountGrade;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.CompanyNature;
import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.Index;

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
    @Index
    private String accountUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountGrade grade;

    @Column
    @Enumerated(EnumType.STRING)
    private CompanyNature companyNature;

    @Column
    private String specialLine;

    @Column
    private String internetCloud;

    @Column
    private String salesman;

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

    public CompanyNature getCompanyNature() {
        return companyNature;
    }

    public String getSalesman() {
        return salesman;
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

    public void setCompanyNature(CompanyNature companyNature) {
        this.companyNature = companyNature;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public String getSpecialLine() {
        return specialLine;
    }

    public String getInternetCloud() {
        return internetCloud;
    }

    public void setSpecialLine(String specialLine) {
        this.specialLine = specialLine;
    }

    public void setInternetCloud(String internetCloud) {
        this.internetCloud = internetCloud;
    }
}
