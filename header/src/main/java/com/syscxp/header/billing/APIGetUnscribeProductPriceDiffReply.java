package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIGetUnscribeProductPriceDiffReply extends APIReply{

    private BigDecimal cash;  //inventory

    private BigDecimal present; //reFoundMoney;

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getPresent() {
        return present;
    }

    public void setPresent(BigDecimal present) {
        this.present = present;
    }

    public APIGetUnscribeProductPriceDiffReply(){}

    public APIGetUnscribeProductPriceDiffReply(APIGetUnscribeProductPriceDiffReply reply){
        this.setCash(reply.getCash());
        this.setPresent(reply.getPresent());
    }
}
