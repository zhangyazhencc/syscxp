package org.zstack.header.billing;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateOrderEvent  extends APIEvent {
    private  OrderInventory inventory;

    public APICreateOrderEvent(){}

    public APICreateOrderEvent(String apiId){super(apiId);}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
