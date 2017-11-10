package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-21
 */
@RestResponse(allTo = "inventory")
public class APIUpdateNodeEvent extends APIEvent {

    private NodeInventory inventory;

    public APIUpdateNodeEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateNodeEvent() {}

    public NodeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NodeInventory inventory) {
        this.inventory = inventory;
    }
}
