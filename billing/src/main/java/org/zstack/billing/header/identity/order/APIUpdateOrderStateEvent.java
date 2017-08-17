package org.zstack.billing.header.identity.order;

import org.zstack.header.message.APIEvent;

public class APIUpdateOrderStateEvent extends APIEvent{

    private OrderInventory inventory;

    public APIUpdateOrderStateEvent(String apiId) {super(apiId);}

    public APIUpdateOrderStateEvent(){}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
