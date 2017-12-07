package com.syscxp.billing.header.bill;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductType;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MonetaryOrderType {
    private ProductType type;

    private OrderType orderType;

    private BigDecimal payPresentTotal;

    private BigDecimal payCashTotal;

    public MonetaryOrderType(){}
    public MonetaryOrderType(Object[] objs) {
        type = ProductType.valueOf((String) objs[0]);
        orderType = OrderType.valueOf( (String)objs[1]);
        payPresentTotal = (BigDecimal) objs[2];
        payCashTotal = (BigDecimal) objs[3];
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
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

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}
