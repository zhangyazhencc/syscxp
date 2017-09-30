package org.zstack.tunnel.header.switchs;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/9/29
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class PhysicalSwitchUpLinkRefVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String physicalSwitchUuid;

    @Column
    private String portName;

    @Column
    private String uplinkPhysicalSwitchUuid;

    @Column
    private String uplinkPhysicalSwitchPortName;

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

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getUplinkPhysicalSwitchUuid() {
        return uplinkPhysicalSwitchUuid;
    }

    public void setUplinkPhysicalSwitchUuid(String uplinkPhysicalSwitchUuid) {
        this.uplinkPhysicalSwitchUuid = uplinkPhysicalSwitchUuid;
    }

    public String getUplinkPhysicalSwitchPortName() {
        return uplinkPhysicalSwitchPortName;
    }

    public void setUplinkPhysicalSwitchPortName(String uplinkPhysicalSwitchPortName) {
        this.uplinkPhysicalSwitchPortName = uplinkPhysicalSwitchPortName;
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
