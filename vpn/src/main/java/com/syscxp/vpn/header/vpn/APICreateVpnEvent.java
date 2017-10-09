package com.syscxp.vpn.header.vpn;

import com.syscxp.header.message.APIEvent;

public class APICreateVpnEvent extends APIEvent{
    private VpnInventory inventory;

    public APICreateVpnEvent() {
    }

    public APICreateVpnEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
