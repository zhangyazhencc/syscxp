package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3EndPointEvent extends APIEvent {

    private L3EndpointInventory inventory;

    public APIDeleteL3EndPointEvent() {
        super(null);
    }

    public APIDeleteL3EndPointEvent(String apiId) {
        super(apiId);
    }

    public L3EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
