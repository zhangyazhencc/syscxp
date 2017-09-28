package org.zstack.header.billing;

import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIReply;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateOrderReply extends APIReply {
    private  OrderInventory inventory;

    public APICreateOrderReply(){}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }
}
