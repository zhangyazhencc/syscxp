package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetPolicyByResourceUuidReply extends APIReply {

    private List<ResourcePolicyRefInventory> inventories;

    public List<ResourcePolicyRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourcePolicyRefInventory> inventories) {
        this.inventories = inventories;
    }
}
