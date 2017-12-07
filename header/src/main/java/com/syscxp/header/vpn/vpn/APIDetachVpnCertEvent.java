package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIDetachVpnCertEvent extends APIEvent{
    VpnInventory inventory;

    public APIDetachVpnCertEvent() {
    }

    public APIDetachVpnCertEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
