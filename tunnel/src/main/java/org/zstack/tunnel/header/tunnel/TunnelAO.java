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
    @ForeignKey(parentEntityClass = NetWorkVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String netWorkUuid;

    @Column
    private String name;

    @Column
    private Integer bandwidth;

    @Column
    private Double distance;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelState state;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelStatus status;

    @Column
    @ForeignKey(parentEntityClass = InterfaceEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String interfaceAUuid;

    @Column
    private Integer aVlan;

    @Column
    private Integer enableQinqA;

    @Column
    @ForeignKey(parentEntityClass = InterfaceEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String interfaceZUuid;

    @Column
    private Integer zVlan;

    @Column
    private Integer enableQinqZ;

    @Column
    private Integer isMonitor;

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

    public String getNetWorkUuid() {
        return netWorkUuid;
    }

    public void setNetWorkUuid(String netWorkUuid) {
        this.netWorkUuid = netWorkUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
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

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public Integer getaVlan() {
        return aVlan;
    }

    public void setaVlan(Integer aVlan) {
        this.aVlan = aVlan;
    }

    public Integer getEnableQinqA() {
        return enableQinqA;
    }

    public void setEnableQinqA(Integer enableQinqA) {
        this.enableQinqA = enableQinqA;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public Integer getzVlan() {
        return zVlan;
    }

    public void setzVlan(Integer zVlan) {
        this.zVlan = zVlan;
    }

    public Integer getEnableQinqZ() {
        return enableQinqZ;
    }

    public void setEnableQinqZ(Integer enableQinqZ) {
        this.enableQinqZ = enableQinqZ;
    }

    public Integer getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(Integer isMonitor) {
        this.isMonitor = isMonitor;
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
}
