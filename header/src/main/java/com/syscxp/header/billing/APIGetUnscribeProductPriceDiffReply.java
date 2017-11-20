package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIGetUnscribeProductPriceDiffReply extends APIReply{

    private BigDecimal inventory;

    public BigDecimal getInventory() {
        return inventory;
    }

    public void setInventory(BigDecimal inventory) {
        this.inventory = inventory;
    }

    public APIGetUnscribeProductPriceDiffReply(){}

    public APIGetUnscribeProductPriceDiffReply(APIGetUnscribeProductPriceDiffReply reply){
        this.setInventory(reply.getInventory());
    }
}
