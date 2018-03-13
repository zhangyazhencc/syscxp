package com.syscxp.core.config;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 */
public class APIQueryGlobalConfigReply extends APIQueryReply {
    private List<GlobalConfigInventory> inventories;

    public List<GlobalConfigInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<GlobalConfigInventory> inventories) {
        this.inventories = inventories;
    }

    public static APIQueryGlobalConfigReply __example__() {
        APIQueryGlobalConfigReply reply = new APIQueryGlobalConfigReply();


        return reply;
    }

}
