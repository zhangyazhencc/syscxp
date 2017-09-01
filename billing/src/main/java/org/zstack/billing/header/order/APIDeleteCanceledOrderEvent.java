package org.zstack.billing.header.order;

import org.zstack.header.message.APIEvent;

@Deprecated
public class APIDeleteCanceledOrderEvent extends APIEvent {
    private OrderInventory inventory;

    public APIDeleteCanceledOrderEvent(String apiId) {super(apiId);}

    public APIDeleteCanceledOrderEvent(){}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
