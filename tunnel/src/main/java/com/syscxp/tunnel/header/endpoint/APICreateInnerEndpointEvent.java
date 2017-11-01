package com.syscxp.tunnel.header.endpoint;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/1
 */
@RestResponse(allTo = "inventory")
public class APICreateInnerEndpointEvent extends APIEvent {
    private InnerConnectedEndpointInventory inventory;

    public APICreateInnerEndpointEvent(){}

    public APICreateInnerEndpointEvent(String apiId){super(apiId);}

    public InnerConnectedEndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(InnerConnectedEndpointInventory inventory) {
        this.inventory = inventory;
    }
}
