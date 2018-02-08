package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APICreateL3EndPointEvent extends APIEvent {

    private L3EndPointInventory inventory;

    public APICreateL3EndPointEvent(){super(null);}

    public APICreateL3EndPointEvent(String apiId){super(apiId);}

    public L3EndPointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndPointInventory inventory) {
        this.inventory = inventory;
    }
}
