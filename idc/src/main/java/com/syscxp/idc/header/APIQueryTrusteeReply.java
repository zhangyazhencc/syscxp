package com.syscxp.idc.header;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryTrusteeReply extends APIQueryReply {

    List<TrusteeInventory> inventories;

    public List<TrusteeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TrusteeInventory> inventories) {
        this.inventories = inventories;
    }
}
