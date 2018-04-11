package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateL3VpnEvent extends APIEvent{
    L3VpnInventory inventory;

    public APIUpdateL3VpnEvent() {
    }

    public APIUpdateL3VpnEvent(String apiId) {
        super(apiId);
    }

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
