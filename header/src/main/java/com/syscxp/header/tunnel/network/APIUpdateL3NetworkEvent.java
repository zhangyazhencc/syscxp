package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIUpdateL3NetworkEvent extends APIEvent {

    private L3NetworkInventory inventory;

    public APIUpdateL3NetworkEvent(){super(null);}

    public APIUpdateL3NetworkEvent(String apiId){super(apiId);}

    public L3NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3NetworkInventory inventory) {
        this.inventory = inventory;
    }
}
