package com.syscxp.header.billing;

import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIReply;

public class APIUpdateOrderExpiredTimeReply extends APIReply {
    private OrderInventory inventory;

    public APIUpdateOrderExpiredTimeReply(){}


    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}