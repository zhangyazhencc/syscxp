package com.syscxp.header.tunnel.node;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/1
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class ZoneVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String name;

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

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
