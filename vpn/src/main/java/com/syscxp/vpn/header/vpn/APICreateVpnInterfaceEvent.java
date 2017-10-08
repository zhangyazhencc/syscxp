package com.syscxp.vpn.header.vpn;

import com.syscxp.header.message.APIEvent;

public class APICreateVpnInterfaceEvent extends APIEvent{
    private VpnInterfaceInventory inventory;

    public APICreateVpnInterfaceEvent() {
    }

    public APICreateVpnInterfaceEvent(String apiId) {
        super(apiId);
    }

    public VpnInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
