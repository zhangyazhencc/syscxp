package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIResetVpnSecretKeyEvent extends APIEvent {


    public APIResetVpnSecretKeyEvent() {
    }

    public APIResetVpnSecretKeyEvent(String apiId) {
        super(apiId);
    }

    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
