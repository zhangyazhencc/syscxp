package org.zstack.billing.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class APICreateOrderMsg extends APIMessage {
    @APIParam(nonempty = true)
    private String accountUuid;

    @APIParam(nonempty = true)
    private OrderType orderType;

    @APIParam(nonempty = true)
    private OrderState orderState;

    @APIParam(nonempty = true)
    private Timestamp productEffectTimeStart;

    @APIParam(nonempty = true)
    private Timestamp productEffectTimeEnd;

    @APIParam(nonempty = true)
    private String productName;

    @APIParam(nonempty = true)
    private ProductType productType;

    @APIParam(nonempty = true,numberRange={0,100})
    private BigDecimal productDiscount;

    @APIParam(nonempty = true)
    private ProductChargeModel productChargeModel;

    @APIParam(nonempty = true)
    private String productDescription;

    @APIParam(nonempty = true,numberRange={0,})
    private BigDecimal total;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
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
}
