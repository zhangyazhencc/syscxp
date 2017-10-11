package com.syscxp.billing.header.balance;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryDealDetailReply extends APIQueryReply {

    private List<DealDetailInventory> inventories;

    public List<DealDetailInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<DealDetailInventory> inventories) {
        this.inventories = inventories;
    }
}