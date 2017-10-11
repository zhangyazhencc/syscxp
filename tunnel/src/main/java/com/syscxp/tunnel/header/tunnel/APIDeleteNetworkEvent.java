package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-14
 */
public class APIDeleteNetworkEvent extends APIEvent {
    private NetworkInventory inventory;

    public APIDeleteNetworkEvent(String apiId) {super(apiId);}

    public APIDeleteNetworkEvent(){}

    public NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetworkInventory inventory) {
        this.inventory = inventory;
    }
}