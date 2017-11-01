package com.syscxp.billing.header.balance;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryAccountDiscountReply extends APIQueryReply {

    private List<AccountDiscountInventory> inventories;

    public List<AccountDiscountInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountDiscountInventory> inventories) {
        this.inventories = inventories;
    }
}