package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.vpn.vpn.VpnInventory;

public class APICreateL3VpnEvent extends APIEvent{
    private VpnInventory inventory;

    public APICreateL3VpnEvent() {
    }

    public APICreateL3VpnEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
