package com.syscxp.vpn.header.vpn;

import com.syscxp.header.message.APIEvent;

public class APICreateVpnRouteEvent extends APIEvent{
    private VpnRouteInventory inventory;

    public APICreateVpnRouteEvent() {
    }

    public APICreateVpnRouteEvent(String apiId) {
        super(apiId);
    }

    public VpnRouteInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnRouteInventory inventory) {
        this.inventory = inventory;
    }
}
