package com.syscxp.billing.header.balance;

import com.syscxp.header.message.APIReply;

public class APIRechargeReply  extends APIReply {

    private String inventory;

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
}
