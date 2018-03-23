package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APICreateL3EndPointEvent extends APIEvent {

    private L3EndpointInventory inventory;

    public APICreateL3EndPointEvent(){super(null);}

    public APICreateL3EndPointEvent(String apiId){super(apiId);}

    public L3EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
