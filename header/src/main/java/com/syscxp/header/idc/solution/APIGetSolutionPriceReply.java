package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

/**
 * Create by DCY on 2018/4/9
 */
public class APIGetSolutionPriceReply extends APIReply {

    private BigDecimal cost;

    private BigDecimal discount;

    private BigDecimal shareDiscount;

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShareDiscount() {
        return shareDiscount;
    }

    public void setShareDiscount(BigDecimal shareDiscount) {
        this.shareDiscount = shareDiscount;
    }
}
