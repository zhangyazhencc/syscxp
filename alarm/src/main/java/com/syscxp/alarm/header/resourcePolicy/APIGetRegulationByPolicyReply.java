package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetRegulationByPolicyReply extends APIReply {

    private List<RegulationInventory> inventories;

    public List<RegulationInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<RegulationInventory> inventories) {
        this.inventories = inventories;
    }
}
