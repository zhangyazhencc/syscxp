package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.vpn.l3vpn.L3VpnInventory;

public class APIAttachL3VpnCertEvent extends APIEvent {
    L3VpnInventory inventory;

    public APIAttachL3VpnCertEvent() {
    }

    public APIAttachL3VpnCertEvent(String apiId) {
        super(apiId);
    }

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
