package org.zstack.account.header.identity.VO;

import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;
import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.Index;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AccountVO {
    @Id
    @Column
    private String uuid;
    
    @Column
    @Index
    private String name;

    @Column
    private String password;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus trueName;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus company;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus department;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus industry;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus description;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column
    private Timestamp createDate;
    
    @Column
    private Timestamp lastOpDate;


    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
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

    public AccountStatus getTrueName() {
        return trueName;
    }

    public AccountStatus getCompany() {
        return company;
    }

    public AccountStatus getDepartment() {
        return department;
    }

    public AccountStatus getIndustry() {
        return industry;
    }

    public AccountStatus getDescription() {
        return description;
    }

    public void setTrueName(AccountStatus trueName) {
        this.trueName = trueName;
    }

    public void setCompany(AccountStatus company) {
        this.company = company;
    }

    public void setDepartment(AccountStatus department) {
        this.department = department;
    }

    public void setIndustry(AccountStatus industry) {
        this.industry = industry;
    }

    public void setDescription(AccountStatus description) {
        this.description = description;
    }
}
