package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3EndpointEvent extends APIEvent {

    private L3EndpointInventory inventory;

    public APIDeleteL3EndpointEvent() {
        super(null);
    }

    public APIDeleteL3EndpointEvent(String apiId) {
        super(apiId);
    }

    public L3EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
