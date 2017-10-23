package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryResourceReply  extends APIQueryReply {

    private List<ResourceInventory> inventories;

    public List<ResourceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourceInventory> inventories) {
        this.inventories = inventories;
    }
}
