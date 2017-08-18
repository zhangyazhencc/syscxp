package org.zstack.account.header.identity;


import org.zstack.header.identity.AccountAuthentication;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;
import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.Index;
import org.zstack.header.identity.AccountGrade;
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
    private AccountAuthentication phoneStatus;

    @Column
    private AccountAuthentication emailStatus;

    @Column
    private String trueName;

    @Column
    private String company;

    @Column
    private String industry;

    @Column
    private String description;

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

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getTrueName() {
        return trueName;
    }

    public String getCompany() {
        return company;
    }

    public String getIndustry() {
        return industry;
    }

    public String getDescription() {
        return description;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public AccountType getType() {
        return type;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public AccountAuthentication getPhoneStatus() {
        return phoneStatus;
    }

    public AccountAuthentication getEmailStatus() {
        return emailStatus;
    }

    public void setPhoneStatus(AccountAuthentication phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public void setEmailStatus(AccountAuthentication emailStatus) {
        this.emailStatus = emailStatus;
    }
}