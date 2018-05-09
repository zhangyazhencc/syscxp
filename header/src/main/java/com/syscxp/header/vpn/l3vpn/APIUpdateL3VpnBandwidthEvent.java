package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.vpn.l3vpn.L3VpnInventory;

public class APIUpdateL3VpnBandwidthEvent extends APIEvent {
    L3VpnInventory inventory;

    public APIUpdateL3VpnBandwidthEvent() {
    }

    public APIUpdateL3VpnBandwidthEvent(String apiId) {
        super(apiId);
    }

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
