package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.tunnel.switchs.SwitchPortType;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/30
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class PortOfferingVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private SwitchPortType type;

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

    public SwitchPortType getType() {
        return type;
    }

    public void setType(SwitchPortType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
