package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIUpdateL3EndPointEvent extends APIEvent {

    private L3EndPointInventory inventory;

    public APIUpdateL3EndPointEvent(){super(null);}

    public APIUpdateL3EndPointEvent(String apiId){super(apiId);}

    public L3EndPointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndPointInventory inventory) {
        this.inventory = inventory;
    }
}
