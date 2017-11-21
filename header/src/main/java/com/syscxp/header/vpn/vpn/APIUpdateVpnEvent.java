package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnEvent() {
    }

    public APIUpdateVpnEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
