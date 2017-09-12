package org.zstack.account.header.user;

import org.zstack.account.header.account.AccountVO;
import org.zstack.account.header.identity.RoleVO;
import org.zstack.header.identity.ValidateStatus;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table
public class UserVO {
    @Id
    @Column
    private String uuid;
    
    @Column
    @ForeignKey(parentEntityClass = AccountVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String accountUuid;
    
    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    @Enumerated(EnumType.STRING)
    private ValidateStatus emailStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private ValidateStatus phoneStatus;

    @Column
    private String trueName;

    @Column
    private String department;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToMany(cascade=CascadeType.REFRESH,fetch = FetchType.LAZY)
    @JoinTable(
            name="UserRoleRefVO",
            joinColumns=@JoinColumn(name="userUuid"),
            inverseJoinColumns=@JoinColumn(name="roleUuid")
    )
    private Set<RoleVO> roleSet;

    @Column
    @Enumerated(EnumType.STRING)
    private  UserType userType;

    @Column
    private String description;

    @Column
    private Timestamp createDate;
    
    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTrueName() {
        return trueName;
    }

    public String getDepartment() {
        return department;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public ValidateStatus getEmailStatus() {
        return emailStatus;
    }

    public ValidateStatus getPhoneStatus() {
        return phoneStatus;
    }

    public void setEmailStatus(ValidateStatus emailStatus) {
        this.emailStatus = emailStatus;
    }

    public void setPhoneStatus(ValidateStatus phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public Set<RoleVO> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<RoleVO> roleSet) {
        this.roleSet = roleSet;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
