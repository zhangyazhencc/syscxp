package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

public class APICreateFixedCostProductOrderReply extends APIReply {

    private OrderInventory inventory;

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
