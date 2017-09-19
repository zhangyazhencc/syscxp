package org.zstack.billing.header.bill;

import org.zstack.header.billing.ProductType;

import java.math.BigDecimal;

public class Monetary {

    private ProductType type;

    private Integer categoryCount;

    private BigDecimal payPresentTotal;

    private BigDecimal payCashTotal;

    public Monetary(){}
    public Monetary(Object[] objs) {
        type = ProductType.valueOf((String) objs[0]);
        categoryCount = (Integer) objs[1];
        payPresentTotal = (BigDecimal) objs[2];
        payCashTotal = (BigDecimal) objs[3];
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Integer getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Integer categoryCount) {
        this.categoryCount = categoryCount;
    }

    public BigDecimal getPayPresentTotal() {
        return payPresentTotal;
    }

    public void setPayPresentTotal(BigDecimal payPresentTotal) {
        this.payPresentTotal = payPresentTotal;
    }

    public BigDecimal getPayCashTotal() {
        return payCashTotal;
    }

    public void setPayCashTotal(BigDecimal payCashTotal) {
        this.payCashTotal = payCashTotal;
    }
}
