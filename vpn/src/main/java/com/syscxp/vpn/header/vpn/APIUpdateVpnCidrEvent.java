package com.syscxp.vpn.header.vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnCidrEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnCidrEvent() {
    }

    public APIUpdateVpnCidrEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
