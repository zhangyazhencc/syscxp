package com.syscxp.header.billing;

import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateOrderExpiredTimeEvent   extends APIEvent {
    private OrderInventory inventory;

    public APIUpdateOrderExpiredTimeEvent(){}

    public APIUpdateOrderExpiredTimeEvent(String apiId){super(apiId);}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}