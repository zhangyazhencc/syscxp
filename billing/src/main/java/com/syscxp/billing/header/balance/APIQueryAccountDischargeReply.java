package com.syscxp.billing.header.balance;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryAccountDischargeReply extends APIQueryReply {

    private List<AccountDischargeInventory> inventories;

    public List<AccountDischargeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountDischargeInventory> inventories) {
        this.inventories = inventories;
    }
}