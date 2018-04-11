package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;

public class APIDetachL3VpnCertEvent extends APIEvent{
    L3VpnInventory inventory;

    public APIDetachL3VpnCertEvent() {
    }

    public APIDetachL3VpnCertEvent(String apiId) {
        super(apiId);
    }

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
