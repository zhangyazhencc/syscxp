package com.syscxp.header.vpn.host;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnHostPortEvent extends APIEvent{
    VpnHostInventory inventory;

    public APIUpdateVpnHostPortEvent() {
    }

    public APIUpdateVpnHostPortEvent(String apiId) {
        super(apiId);
    }

    public VpnHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnHostInventory inventory) {
        this.inventory = inventory;
    }
}
