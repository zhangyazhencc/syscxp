package com.syscxp.billing.header.order;

import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryOrderReply  extends APIQueryReply {

    private List<OrderInventory> inventories;

    public List<OrderInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OrderInventory> inventories) {
        this.inventories = inventories;
    }
}
