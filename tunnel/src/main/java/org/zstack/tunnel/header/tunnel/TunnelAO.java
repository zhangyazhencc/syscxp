package org.zstack.tunnel.header.tunnel;

import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-11
 */
@MappedSuperclass
public class TunnelAO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    @ForeignKey(parentEntityClass = NetworkVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String networkUuid;

    @Column
    private String name;

    @Column
    private Long bandwidth;

    @Column
    private Double distance;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelState state;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelMonitorState monitorState;

    @Column
    private Integer months;

    @Column
    private String description;

    @Column
    private Timestamp expiredDate;

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

    public String getNetworkUuid() {
        return networkUuid;
    }

    public void setNetworkUuid(String networkUuid) {
        this.networkUuid = networkUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public TunnelState getState() {
        return state;
    }

    public void setState(TunnelState state) {
        this.state = state;
    }

    public TunnelStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelStatus status) {
        this.status = status;
    }

    public TunnelMonitorState getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(TunnelMonitorState monitorState) {
        this.monitorState = monitorState;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
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
