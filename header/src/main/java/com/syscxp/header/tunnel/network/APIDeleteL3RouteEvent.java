package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3RouteEvent extends APIEvent {

    private L3RouteInventory inventory;

    public APIDeleteL3RouteEvent() {
        super(null);
    }

    public APIDeleteL3RouteEvent(String apiId) {
        super(apiId);
    }

    public L3RouteInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3RouteInventory inventory) {
        this.inventory = inventory;
    }
}
