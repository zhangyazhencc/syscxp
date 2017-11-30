package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.tunnel.node.NodeEO;
import com.syscxp.header.tunnel.tunnel.TunnelEO;
import com.syscxp.header.vo.NoView;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-15.
 * @Description: 速度测试.
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class SpeedRecordsVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String tunnelUuid;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "tunnelUuid", insertable = false, updatable = false)
    @NoView
    private TunnelEO tunnelEO;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "srcNodeUuid", insertable = false, updatable = false)
    @NoView
    private NodeEO srcNodeEO;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "dstNodeUuid", insertable = false, updatable = false)
    @NoView
    private NodeEO dstNodeEO;

    @Column
    private String srcTunnelMonitorUuid;

    @Column
    private String dstTunnelMonitorUuid;

    @Column
    private String srcNodeUuid;

    @Column
    private String dstNodeUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private ProtocolType protocolType;

    @Column
    private Integer duration;

    @Column
    private Integer avgSpeed;

    @Column
    private Integer maxSpeed;

    @Column
    private Integer minSpeed;

    @Column
    @Enumerated(EnumType.STRING)
    private SpeedRecordStatus status;

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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Integer avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Integer getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(Integer minSpeed) {
        this.minSpeed = minSpeed;
    }

    public SpeedRecordStatus getStatus() {
        return status;
    }

    public void setStatus(SpeedRecordStatus status) {
        this.status = status;
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

    public String getSrcTunnelMonitorUuid() {
        return srcTunnelMonitorUuid;
    }

    public void setSrcTunnelMonitorUuid(String srcTunnelMonitorUuid) {
        this.srcTunnelMonitorUuid = srcTunnelMonitorUuid;
    }

    public String getDstTunnelMonitorUuid() {
        return dstTunnelMonitorUuid;
    }

    public void setDstTunnelMonitorUuid(String dstTunnelMonitorUuid) {
        this.dstTunnelMonitorUuid = dstTunnelMonitorUuid;
    }

    public TunnelEO getTunnelEO() {
        return tunnelEO;
    }

    public void setTunnelEO(TunnelEO tunnelEO) {
        this.tunnelEO = tunnelEO;
    }

    public NodeEO getSrcNodeEO() {
        return srcNodeEO;
    }

    public void setSrcNodeEO(NodeEO srcNodeEO) {
        this.srcNodeEO = srcNodeEO;
    }

    public NodeEO getDstNodeEO() {
        return dstNodeEO;
    }

    public void setDstNodeEO(NodeEO dstNodeEO) {
        this.dstNodeEO = dstNodeEO;
    }

    public String getSrcNodeUuid() {
        return srcNodeUuid;
    }

    public void setSrcNodeUuid(String srcNodeUuid) {
        this.srcNodeUuid = srcNodeUuid;
    }

    public String getDstNodeUuid() {
        return dstNodeUuid;
    }

    public void setDstNodeUuid(String dstNodeUuid) {
        this.dstNodeUuid = dstNodeUuid;
    }
}
