package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateL3VpnIPEvent extends APIEvent {


    public APIUpdateL3VpnIPEvent() {
    }

    public APIUpdateL3VpnIPEvent(String apiId) {
        super(apiId);
    }

    private L3VpnInventory inventory;

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
