package org.zstack.tunnel.header.monitor;


import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.tunnel.header.tunnel.TunnelInterfaceVO;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: 通道监控.
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class TunnelMonitorVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    private String hostAUuid;

    @Column
    private String monitorAIp;

    @Column
    private String hostZUuid;

    @Column
    private String monitorZIp;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelMonitorStatus status;

    @Column
    private String msg;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    //private List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOList;

    @OneToMany
    @JoinColumn(name = "tunnelMonitorUuid", insertable = false, updatable = false)
    private List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOList = new ArrayList<TunnelMonitorInterfaceVO>();

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

    public String getHostAUuid() {
        return hostAUuid;
    }

    public void setHostAUuid(String hostAUuid) {
        this.hostAUuid = hostAUuid;
    }

    public String getMonitorAIp() {
        return monitorAIp;
    }

    public void setMonitorAIp(String monitorAIp) {
        this.monitorAIp = monitorAIp;
    }

    public String getHostZUuid() {
        return hostZUuid;
    }

    public void setHostZUuid(String hostZUuid) {
        this.hostZUuid = hostZUuid;
    }

    public String getMonitorZIp() {
        return monitorZIp;
    }

    public void setMonitorZIp(String monitorZIp) {
        this.monitorZIp = monitorZIp;
    }

    public TunnelMonitorStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelMonitorStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public List<TunnelMonitorInterfaceVO> getTunnelMonitorInterfaceVOList() {
        return tunnelMonitorInterfaceVOList;
    }

    public void setTunnelMonitorInterfaceVOList(List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOList) {
        this.tunnelMonitorInterfaceVOList = tunnelMonitorInterfaceVOList;
    }
}
