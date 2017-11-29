package com.syscxp.billing.header.report;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReportOrderData {

    private String payTime;
    private OrderType type;
    private ProductType productType;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private BigDecimal payPresent;
    private BigDecimal payCash;
    public ReportOrderData(){}
    public ReportOrderData(Object[] objs) {
        payTime = (String) objs[0];
        type = (OrderType) objs[1];
        productType = (ProductType) objs[2];
        originalPrice = (BigDecimal) objs[3];
        price = (BigDecimal) objs[4];
        payPresent = (BigDecimal) objs[5];
        payCash = (BigDecimal) objs[6];
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
