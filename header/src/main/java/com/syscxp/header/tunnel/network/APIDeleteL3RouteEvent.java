package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3RouteEvent extends APIEvent {

    private L3EndPointInventory inventory;

    public APIDeleteL3RouteEvent() {
        super(null);
    }

    public APIDeleteL3RouteEvent(String apiId) {
        super(apiId);
    }

    public L3EndPointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndPointInventory inventory) {
        this.inventory = inventory;
    }
}
