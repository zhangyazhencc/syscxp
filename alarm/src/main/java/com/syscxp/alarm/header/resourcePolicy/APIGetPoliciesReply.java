package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetPoliciesReply extends APIReply {

    private List<PolicyInventory> inventories;

    private long count;

    public List<PolicyInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PolicyInventory> inventories) {
        this.inventories = inventories;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
