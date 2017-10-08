package com.syscxp.vpn.header.vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnExpireDateEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnExpireDateEvent() {
    }

    public APIUpdateVpnExpireDateEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
