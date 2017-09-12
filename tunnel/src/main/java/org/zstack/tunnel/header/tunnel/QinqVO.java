package org.zstack.tunnel.header.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-11
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class QinqVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    private String interfaceUuid;

    @Column
    private Integer startVlan;

    @Column
    private Integer endVlan;

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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
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
