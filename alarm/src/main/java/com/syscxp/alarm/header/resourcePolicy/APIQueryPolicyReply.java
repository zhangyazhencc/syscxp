package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryPolicyReply extends APIQueryReply {
    private List<PolicyInventory> inventories;

    public List<PolicyInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PolicyInventory> inventories) {
        this.inventories = inventories;
    }

}
