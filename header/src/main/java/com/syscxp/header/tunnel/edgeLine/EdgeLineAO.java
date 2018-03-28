package com.syscxp.header.tunnel.edgeLine;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/1/9
 */
@MappedSuperclass
public class EdgeLineAO {
    @Id
    @Column
    private String uuid;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long number;

    @Column
    private String accountUuid;

    @Column
    private String interfaceUuid;

    @Column
    private String endpointUuid;

    @Column
    private String type;

    @Column
    private String destinationInfo;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private EdgeLineState state;

    @Column
    private Integer costPrices;

    @Column
    private Integer prices;

    @Column
    private Integer fixedCost;

    @Column
    private String implementType;

    @Column
    private Timestamp expireDate;

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

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EdgeLineState getState() {
        return state;
    }

    public void setState(EdgeLineState state) {
        this.state = state;
    }

    public Integer getPrices() {
        return prices;
    }

    public void setPrices(Integer prices) {
        this.prices = prices;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(String destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getImplementType() {
        return implementType;
    }

    public void setImplementType(String implementType) {
        this.implementType = implementType;
    }

    public Integer getCostPrices() {
        return costPrices;
    }

    public void setCostPrices(Integer costPrices) {
        this.costPrices = costPrices;
    }

    public Integer getFixedCost() {
        return fixedCost;
    }

    public void setFixedCost(Integer fixedCost) {
        this.fixedCost = fixedCost;
    }
}
