package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;

public class APIResetL3VpnSecretKeyEvent extends APIEvent {


    public APIResetL3VpnSecretKeyEvent() {
    }

    public APIResetL3VpnSecretKeyEvent(String apiId) {
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
