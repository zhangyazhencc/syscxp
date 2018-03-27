package com.syscxp.trustee.header;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryTrusteeDetailReply extends APIQueryReply {

    List<TrusteeDetailInventory> inventories;

    public List<TrusteeDetailInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TrusteeDetailInventory> inventories) {
        this.inventories = inventories;
    }
}
