package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnStateEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnStateEvent() {
    }

    public APIUpdateVpnStateEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
