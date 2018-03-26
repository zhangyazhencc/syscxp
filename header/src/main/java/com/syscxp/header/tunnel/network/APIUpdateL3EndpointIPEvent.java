package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/3/12
 */
public class APIUpdateL3EndpointIPEvent extends APIEvent {
    private L3EndpointInventory inventory;

    public APIUpdateL3EndpointIPEvent(){super(null);}

    public APIUpdateL3EndpointIPEvent(String apiId){super(apiId);}

    public L3EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
