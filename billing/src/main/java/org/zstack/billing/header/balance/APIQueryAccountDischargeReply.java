package org.zstack.billing.header.balance;

import org.zstack.header.query.APIQueryReply;

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