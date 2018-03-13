package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-21
 */
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
