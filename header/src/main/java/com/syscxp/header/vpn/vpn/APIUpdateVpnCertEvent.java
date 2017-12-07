package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIUpdateVpnCertEvent extends APIEvent{
    VpnCertInventory inventory;

    public APIUpdateVpnCertEvent() {
    }

    public APIUpdateVpnCertEvent(String apiId) {
        super(apiId);
    }

    public VpnCertInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnCertInventory inventory) {
        this.inventory = inventory;
    }
}