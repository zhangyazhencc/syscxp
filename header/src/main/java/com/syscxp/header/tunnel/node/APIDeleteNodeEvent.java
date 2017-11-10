package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-13.
 * @Description: .
 */
public class APIDeleteNodeEvent extends APIEvent {
    private NodeInventory inventory;

    public APIDeleteNodeEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteNodeEvent() {
    }

    public NodeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NodeInventory inventory) {
        this.inventory = inventory;
    }
}
