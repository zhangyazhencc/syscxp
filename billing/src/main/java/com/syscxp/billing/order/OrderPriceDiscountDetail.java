package com.syscxp.billing.order;

import java.math.BigDecimal;

public class OrderPriceDiscountDetail {

    private String configName;

    private BigDecimal originalPrice;

    private BigDecimal realPayPrice;

    private int discount;

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getRealPayPrice() {
        return realPayPrice;
    }

    public void setRealPayPrice(BigDecimal realPayPrice) {
        this.realPayPrice = realPayPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }
}
