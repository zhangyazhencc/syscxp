package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIChangeVpnCertEvent extends APIEvent{
    VpnInventory inventory;

    public APIChangeVpnCertEvent() {
    }

    public APIChangeVpnCertEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
