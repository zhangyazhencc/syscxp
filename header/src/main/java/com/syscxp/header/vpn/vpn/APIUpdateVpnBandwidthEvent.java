package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnBandwidthEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnBandwidthEvent() {
    }

    public APIUpdateVpnBandwidthEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
