package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIResetCertificateEvent extends APIEvent {
    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }

    public APIResetCertificateEvent() {
    }

    public APIResetCertificateEvent(String apiId) {
        super(apiId);
    }

}
