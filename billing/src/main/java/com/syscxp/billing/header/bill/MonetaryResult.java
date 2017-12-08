package com.syscxp.billing.header.bill;

import com.syscxp.header.billing.ProductType;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MonetaryResult {
    private ProductType type;
    private BigInteger categoryCount;
    private BigDecimal deductionCash;
    private BigDecimal deductionPresent;
    private BigDecimal refundCash;
    private BigDecimal refundPresent;

    public MonetaryResult(){}
    public MonetaryResult(Object[] objs) {
        type = ProductType.valueOf((String) objs[0]);
        categoryCount = (BigInteger) objs[1];
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public BigInteger getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(BigInteger categoryCount) {
        this.categoryCount = categoryCount;
    }

    public BigDecimal getDeductionCash() {
        return deductionCash;
    }

    public void setDeductionCash(BigDecimal deductionCash) {
        this.deductionCash = deductionCash;
    }

    public BigDecimal getDeductionPresent() {
        return deductionPresent;
    }

    public void setDeductionPresent(BigDecimal deductionPresent) {
        this.deductionPresent = deductionPresent;
    }

    public BigDecimal getRefundCash() {
        return refundCash;
    }

    public void setRefundCash(BigDecimal refundCash) {
        this.refundCash = refundCash;
    }

    public BigDecimal getRefundPresent() {
        return refundPresent;
    }

    public void setRefundPresent(BigDecimal refundPresent) {
        this.refundPresent = refundPresent;
    }
}
