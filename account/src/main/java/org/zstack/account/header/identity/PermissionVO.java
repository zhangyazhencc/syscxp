package org.zstack.account.header.identity;

import org.zstack.header.identity.PermissionType;
import org.zstack.header.identity.PermissionVisible;

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
    private String permission;

    @Column
    private String description;

    @Column
    private int sortId;

    @Column
    @Enumerated(EnumType.STRING)
    private PermissionType type;

    @Column
    @Enumerated(EnumType.STRING)
    private PermissionVisible visible;

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

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public String getPermission() {
        return permission;
    }

    public int getSortId() {
        return sortId;
    }

    public PermissionType getType() {
        return type;
    }

    public PermissionVisible getVisible() {
        return visible;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public void setVisible(PermissionVisible visible) {
        this.visible = visible;
    }
}
