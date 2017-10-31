package com.syscxp.header.billing;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class OrderVO {
    @Id
    @Column
    private String uuid;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column
    private Timestamp payTime;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Column
    private BigDecimal payPresent;

    @Column
    private BigDecimal payCash;

    @Column
    private String accountUuid;

    @Column
    private Timestamp productEffectTimeStart;

    @Column
    private Timestamp productEffectTimeEnd;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private String productName;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductChargeModel productChargeModel;

    @Column
    private String descriptionData;
    @Column
    private String callBackData;;

    @Column
    private BigDecimal price;

    @Column
    private BigDecimal originalPrice;

    @Column
    private String productUuid;

    @Column
    private int duration;

    @Column
    private int productStatus;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public BigDecimal getPayPresent() {
        return payPresent;
    }

    public void setPayPresent(BigDecimal payPresent) {
        this.payPresent = payPresent;
    }

    public BigDecimal getPayCash() {
        return payCash;
    }

    public void setPayCash(BigDecimal payCash) {
        this.payCash = payCash;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Timestamp getProductEffectTimeStart() {
        return productEffectTimeStart;
    }

    public void setProductEffectTimeStart(Timestamp productEffectTimeStart) {
        this.productEffectTimeStart = productEffectTimeStart;
    }

    public Timestamp getProductEffectTimeEnd() {
        return productEffectTimeEnd;
    }

    public void setProductEffectTimeEnd(Timestamp productEffectTimeEnd) {
        this.productEffectTimeEnd = productEffectTimeEnd;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getDescriptionData() {
        return descriptionData;
    }

    public void setDescriptionData(String descriptionData) {
        this.descriptionData = descriptionData;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public String getCallBackData() {
        return callBackData;
    }

    public void setCallBackData(String callBackData) {
        this.callBackData = callBackData;
    }

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }
}
