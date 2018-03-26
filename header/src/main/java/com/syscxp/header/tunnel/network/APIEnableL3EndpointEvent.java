package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/3/20
 */

public class APIEnableL3EndpointEvent extends APIEvent {

    private L3EndpointInventory inventory;

    public APIEnableL3EndpointEvent(){super(null);}

    public APIEnableL3EndpointEvent(String apiId){super(apiId);}

    public L3EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
