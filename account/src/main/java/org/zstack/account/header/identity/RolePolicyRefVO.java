package org.zstack.account.header.identity;


import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

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
}
