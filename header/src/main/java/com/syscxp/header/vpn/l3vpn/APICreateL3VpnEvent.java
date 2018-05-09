package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;

public class APICreateL3VpnEvent extends APIEvent {
    private L3VpnInventory inventory;

    public APICreateL3VpnEvent() {
    }

    public APICreateL3VpnEvent(String apiId) {
        super(apiId);
    }

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
