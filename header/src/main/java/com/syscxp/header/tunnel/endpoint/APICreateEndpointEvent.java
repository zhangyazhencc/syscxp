package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-23
 */
public class APICreateEndpointEvent extends APIEvent {
    private EndpointInventory inventory;

    public APICreateEndpointEvent(){}

    public APICreateEndpointEvent(String apiId){super(apiId);}

    public EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
