package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;

public class APIAttachVpnCertEvent extends APIEvent{
    VpnInventory inventory;

    public APIAttachVpnCertEvent() {
    }

    public APIAttachVpnCertEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
