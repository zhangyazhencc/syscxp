package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetPoliciesByResourceReply extends APIReply {
    private List<PolicyInventory> inventories;

    public List<PolicyInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PolicyInventory> inventories) {
        this.inventories = inventories;
    }
}
