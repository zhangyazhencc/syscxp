package com.syscxp.billing.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderTempProp {

    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private List<String> productPriceUnitUuids = new ArrayList<>();
    private List<OrderPriceDiscountDetail> orderPriceDiscountDetails = new ArrayList<>();

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public List<String> getProductPriceUnitUuids() {
        return productPriceUnitUuids;
    }

    public void setProductPriceUnitUuids(List<String> productPriceUnitUuids) {
        this.productPriceUnitUuids = productPriceUnitUuids;
    }

    public List<OrderPriceDiscountDetail> getOrderPriceDiscountDetails() {
        return orderPriceDiscountDetails;
    }

    public void setOrderPriceDiscountDetails(List<OrderPriceDiscountDetail> orderPriceDiscountDetails) {
        this.orderPriceDiscountDetails = orderPriceDiscountDetails;
    }
}
