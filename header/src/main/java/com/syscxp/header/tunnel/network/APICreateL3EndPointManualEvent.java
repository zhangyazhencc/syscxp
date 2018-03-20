package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/3/20
 */
public class APICreateL3EndPointManualEvent extends APIEvent {
    private L3EndPointInventory inventory;

    public APICreateL3EndPointManualEvent(){super(null);}

    public APICreateL3EndPointManualEvent(String apiId){super(apiId);}

    public L3EndPointInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndPointInventory inventory) {
        this.inventory = inventory;
    }
}
