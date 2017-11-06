package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetComparisonRuleListReply extends APIReply {
    private List<ComparisonRuleInventory> inventories;

    public List<ComparisonRuleInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ComparisonRuleInventory> inventories) {
        this.inventories = inventories;
    }
}
