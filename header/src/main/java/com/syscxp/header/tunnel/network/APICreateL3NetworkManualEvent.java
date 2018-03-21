package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/3/20
 */
public class APICreateL3NetworkManualEvent extends APIEvent {
    private L3NetworkInventory inventory;

    public APICreateL3NetworkManualEvent(){super(null);}

    public APICreateL3NetworkManualEvent(String apiId){super(apiId);}

    public L3NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3NetworkInventory inventory) {
        this.inventory = inventory;
    }
}
