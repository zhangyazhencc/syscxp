package org.zstack.account.header.user;


import org.zstack.account.header.identity.RoleVO;
import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class UserRoleRefVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @ForeignKey(parentEntityClass = UserVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String userUuid;

    @Column
    @ForeignKey(parentEntityClass = RoleVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String roleUuid;

    @Column
    private Timestamp createDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

}
