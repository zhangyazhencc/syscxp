package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/3/20
 */
public class APIDisableL3EndPointEvent extends APIEvent {
    private L3EndpointInventory inventory;

    public APIDisableL3EndPointEvent(){super(null);}

    public APIDisableL3EndPointEvent(String apiId){super(apiId);}

    public L3EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
