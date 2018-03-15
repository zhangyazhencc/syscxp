package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3EndPointEvent extends APIEvent {

    private L3EndPointInventory inventory;

    public APIDeleteL3EndPointEvent() {
        super(null);
    }

    public APIDeleteL3EndPointEvent(String apiId) {
        super(apiId);
    }

    public L3EndPointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndPointInventory inventory) {
        this.inventory = inventory;
    }
}
