package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-21
 */
public class APICreateNodeEvent extends APIEvent {
    private NodeInventory inventory;

    public APICreateNodeEvent(){}

    public APICreateNodeEvent(String apiId){super(apiId);}

    public NodeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NodeInventory inventory) {
        this.inventory = inventory;
    }
}
