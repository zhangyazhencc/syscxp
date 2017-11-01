package com.syscxp.tunnel.header.endpoint;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2017/11/1
 */
public class APIDeleteInnerEndpointEvent extends APIEvent {
    private InnerConnectedEndpointInventory inventory;

    public APIDeleteInnerEndpointEvent(){}

    public APIDeleteInnerEndpointEvent(String apiId){super(apiId);}

    public InnerConnectedEndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(InnerConnectedEndpointInventory inventory) {
        this.inventory = inventory;
    }
}
