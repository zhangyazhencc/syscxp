package com.syscxp.billing.header.order;

import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateOrderExpenseEvent extends APIEvent {
    private OrderInventory inventory;

    public APIUpdateOrderExpenseEvent(String apiId) {super(apiId);}

    public APIUpdateOrderExpenseEvent(){}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
