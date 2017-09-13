package org.zstack.billing.header.order;

import org.zstack.header.message.APIEvent;

public class APIUpdateOrderExpiredTimeEvent   extends APIEvent {
    private  OrderInventory inventory;

    public APIUpdateOrderExpiredTimeEvent(){}

    public APIUpdateOrderExpiredTimeEvent(String apiId){super(apiId);}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}