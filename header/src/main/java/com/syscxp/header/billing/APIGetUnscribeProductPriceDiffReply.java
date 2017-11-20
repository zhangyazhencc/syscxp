package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIGetUnscribeProductPriceDiffReply extends APIReply{

    private BigDecimal inventory;

    private BigDecimal reFoundMoney;

    public BigDecimal getInventory() {
        return inventory;
    }

    public void setInventory(BigDecimal inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getReFoundMoney() {
        return reFoundMoney;
    }

    public void setReFoundMoney(BigDecimal reFoundMoney) {
        this.reFoundMoney = reFoundMoney;
    }

    public APIGetUnscribeProductPriceDiffReply(){}

    public APIGetUnscribeProductPriceDiffReply(APIGetUnscribeProductPriceDiffReply reply){
        this.setInventory(reply.getInventory());
    }
}
