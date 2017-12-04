package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APICreateVpnCertEvent extends APIEvent{
    VpnCertInventory inventory;

    public APICreateVpnCertEvent() {
    }

    public APICreateVpnCertEvent(String apiId) {
        super(apiId);
    }

    public VpnCertInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnCertInventory inventory) {
        this.inventory = inventory;
    }
}
