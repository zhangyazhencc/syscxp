package com.syscxp.header.vpn.host;

import com.syscxp.header.message.APIEvent;

public class APICreateVpnHostEvent extends APIEvent{
    private VpnHostInventory inventory;

    public APICreateVpnHostEvent() {
    }

    public APICreateVpnHostEvent(String apiId) {
        super(apiId);
    }

    public VpnHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnHostInventory inventory) {
        this.inventory = inventory;
    }
}
