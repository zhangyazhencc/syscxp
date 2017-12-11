package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIGetRenewProductPriceReply extends APIReply {

    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private BigDecimal mayPayTotal; //账户可用资金总额＝现金＋赠送＋信用额度
    private boolean payable;

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

    public BigDecimal getMayPayTotal() {
        return mayPayTotal;
    }

    public void setMayPayTotal(BigDecimal mayPayTotal) {
        this.mayPayTotal = mayPayTotal;
    }

    public boolean isPayable() {
        return payable;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }

    public APIGetRenewProductPriceReply() {
    }

    public APIGetRenewProductPriceReply(APIGetRenewProductPriceReply reply) {
        this.setDiscountPrice(reply.getDiscountPrice());
        this.setMayPayTotal(reply.getMayPayTotal());
        this.setPayable(reply.isPayable());
        this.setOriginalPrice(reply.getOriginalPrice());
    }
}
