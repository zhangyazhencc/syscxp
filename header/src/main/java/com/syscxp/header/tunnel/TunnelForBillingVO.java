package com.syscxp.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/23
 */
@Entity
@Table(name = "TunnelVO")
@Inheritance(strategy = InheritanceType.JOINED)
public class TunnelForBillingVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private Integer vsi;

    @Column
    private String monitorCidr;

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
    private Integer duration;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductChargeModel productChargeModel;

    @Column
    private Integer maxModifies;

    @Column
    private String description;

    @Column
    private Timestamp expireDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

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

    public Integer getVsi() {
        return vsi;
    }

    public void setVsi(Integer vsi) {
        this.vsi = vsi;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
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
