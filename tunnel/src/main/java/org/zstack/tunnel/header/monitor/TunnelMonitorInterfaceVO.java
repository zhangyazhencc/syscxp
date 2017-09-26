package org.zstack.tunnel.header.monitor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-25.
 * @Description: 监控通道两端信息表.
 */
@Entity
@Table
public class TunnelMonitorInterfaceVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelMonitorUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private InterfaceType interfaceType;

    @Column
    private String hostUuid;

    @Column
    private String monitorIp;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

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

    public String getTunnelMonitorUuid() {
        return tunnelMonitorUuid;
    }

    public void setTunnelMonitorUuid(String tunnelMonitorUuid) {
        this.tunnelMonitorUuid = tunnelMonitorUuid;
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

}
