package org.zstack.account.header.identity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/15.
 */

@Entity
@Table
public class PermissionVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String policy;

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

    public String getDescription() {
        return description;
    }

    public String getPolicy() {
        return policy;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
