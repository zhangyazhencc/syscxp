package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;
import java.util.List;

public class APIGetProductPriceReply extends APIReply {

    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private BigDecimal mayPayTotal; //账户可用资金总额＝现金＋赠送＋信用额度
    private boolean payable;

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice.setScale(2, BigDecimal.ROUND_UP);
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice.setScale(2, BigDecimal.ROUND_UP);
    }

    public BigDecimal getMayPayTotal() {
        return mayPayTotal;
    }

    public void setMayPayTotal(BigDecimal mayPayTotal) {
        this.mayPayTotal = mayPayTotal.setScale(2, BigDecimal.ROUND_DOWN);
    }

    public boolean isPayable() {
        return payable = discountPrice.compareTo(mayPayTotal) <= 0;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }

    public APIGetProductPriceReply() {
    }
    public APIGetProductPriceReply(APIGetProductPriceReply reply) {
        this.setDiscountPrice(reply.getDiscountPrice());
        this.setMayPayTotal(reply.getMayPayTotal());
        this.setPayable(reply.isPayable());
        this.setOriginalPrice(reply.getOriginalPrice());
    }
}