package com.syscxp.account.header.identity;


import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table
public class RolePolicyRefVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @ForeignKey(parentEntityClass = RoleVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String roleUuid;

    @Column
    @ForeignKey(parentEntityClass = PolicyVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String policyUuid;

    @Column
    private Timestamp createDate;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="policyUuid",insertable=false,updatable=false)
    private PolicyVO policy;

    public long getId() {
        return id;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public PolicyVO getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyVO policy) {
        this.policy = policy;
    }
}
