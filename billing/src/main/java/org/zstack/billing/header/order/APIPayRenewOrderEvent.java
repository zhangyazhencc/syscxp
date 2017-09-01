package org.zstack.billing.header.order;

import org.zstack.header.message.APIEvent;

@Deprecated
public class APIPayRenewOrderEvent extends APIEvent{

    private OrderInventory inventory;

    public APIPayRenewOrderEvent(String apiId) {super(apiId);}

    public APIPayRenewOrderEvent(){}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
