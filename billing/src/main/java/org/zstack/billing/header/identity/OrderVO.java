package org.zstack.billing.header.identity;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

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
    private OrderType orderType;

    @Column
    private Timestamp payTime;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Column
    private BigDecimal orderPayPresent;

    @Column
    private BigDecimal orderPayCash;

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
    private ProductType productType;

    @Column
    private BigDecimal productDiscount;

    @Column
    private ProductChargeModel productChargeModel;

    @Column
    private String productDescription;

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

    public BigDecimal getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(BigDecimal productDiscount) {
        this.productDiscount = productDiscount;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public BigDecimal getOrderPayPresent() {
        return orderPayPresent;
    }

    public void setOrderPayPresent(BigDecimal orderPayPresent) {
        this.orderPayPresent = orderPayPresent;
    }

    public BigDecimal getOrderPayCash() {
        return orderPayCash;
    }

    public void setOrderPayCash(BigDecimal orderPayCash) {
        this.orderPayCash = orderPayCash;
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
}
