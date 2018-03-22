package com.syscxp.billing.header.bill;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetCurrentMonthBillReply extends APIReply {

    private List<MonetaryResult> inventory;

    public List<MonetaryResult> getInventory() {
        return inventory;
    }

    public void setInventory(List<MonetaryResult> inventory) {
        this.inventory = inventory;
    }

}
