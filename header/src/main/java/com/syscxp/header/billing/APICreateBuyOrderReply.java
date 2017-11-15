package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APICreateBuyOrderReply extends APIReply {

    private List<OrderInventory> inventories;

    public List<OrderInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OrderInventory> inventories) {
        this.inventories = inventories;
    }
}
