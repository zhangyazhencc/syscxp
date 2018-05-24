package com.syscxp.header.tunnel.cloudhub;


import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class CloudHubVO {

    @Id
    @Column
    private String uuid;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long number;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String accountUuid;

    @Column
    private String interfaceUuid;

    @Column
    private String endpointUuid;

    @Column
    private String cloudHubOfferingUuid;

    @Column
    private Long bandwidth;

    @Column
    private Integer tunnelNumber;

    @Column
    private Integer duration;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductChargeModel productChargeModel;

    @Column
    private Integer maxModifies;

    @Column
    private Timestamp expireDate;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCloudHubOfferingUuid() {
        return cloudHubOfferingUuid;
    }

    public void setCloudHubOfferingUuid(String cloudHubOfferingUuid) {
        this.cloudHubOfferingUuid = cloudHubOfferingUuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public Integer getTunnelNumber() {
        return tunnelNumber;
    }

    public void setTunnelNumber(Integer tunnelNumber) {
        this.tunnelNumber = tunnelNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
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
