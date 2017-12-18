package com.syscxp.header.configuration;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryResourceMotifyRecordReply extends APIQueryReply {
    List<ResourceMotifyRecordInventory> inventories;

    public List<ResourceMotifyRecordInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourceMotifyRecordInventory> inventories) {
        this.inventories = inventories;
    }
}
