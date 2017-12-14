package com.syscxp.header.tunnel.aliEdgeRouter;


import javax.persistence.*;
import java.sql.Timestamp;

@MappedSuperclass
public class AliEdgeRouterAO {
    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String tunnelUuid;

    @Column
    private String aliAccountUuid;

    @Column
    private String aliRegionId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String vbrUuid;

    @Column
    private String physicalLineUuid;

    @Column
    private Integer vlan;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @Column
    private boolean isCreateFlag;

    @Column
    @Enumerated(EnumType.STRING)
    private AliEdgeRouterStatus status;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public boolean isCreateFlag() {
        return isCreateFlag;
    }

    public void setCreateFlag(boolean createFlag) {
        isCreateFlag = createFlag;
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

    public String getAliAccountUuid() {
        return aliAccountUuid;
    }

    public void setAliAccountUuid(String aliAccountUuid) {
        this.aliAccountUuid = aliAccountUuid;
    }

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVbrUuid() {
        return vbrUuid;
    }

    public void setVbrUuid(String vbrUuid) {
        this.vbrUuid = vbrUuid;
    }

    public String getPhysicalLineUuid() {
        return physicalLineUuid;
    }

    public void setPhysicalLineUuid(String physicalLineUuid) {
        this.physicalLineUuid = physicalLineUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
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

    public AliEdgeRouterStatus getStatus() {
        return status;
    }

    public void setStatus(AliEdgeRouterStatus status) {
        this.status = status;
    }
}
