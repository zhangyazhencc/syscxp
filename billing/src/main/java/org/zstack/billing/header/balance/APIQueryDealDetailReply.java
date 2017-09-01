package org.zstack.billing.header.balance;

import org.zstack.header.query.APIQueryReply;

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