package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIDownloadVpnCertEvent extends APIEvent {
    VpnCertInventory inventory;

    public APIDownloadVpnCertEvent() {
    }

    public APIDownloadVpnCertEvent(String apiId) {
        super(apiId);
    }

    public VpnCertInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnCertInventory inventory) {
        this.inventory = inventory;
    }
}
