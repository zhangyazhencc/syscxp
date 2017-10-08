package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountVO;
import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table
public class RoleVO {
    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = AccountVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String accountUuid;

    @Column
    private String name;

    @Column
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(
            name="RolePolicyRefVO",
            joinColumns=@JoinColumn(name="roleUuid"),
            inverseJoinColumns=@JoinColumn(name="policyUuid")
    )
    private Set<PolicyVO> policySet;

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

    public Set<PolicyVO> getPolicySet() {
        return policySet;
    }

    public void setPolicySet(Set<PolicyVO> policySet) {
        this.policySet = policySet;
    }
}
