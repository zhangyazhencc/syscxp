package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APICreateVpnEvent extends APIEvent{
    private VpnInventory inventory;

    public APICreateVpnEvent() {
    }

    public APICreateVpnEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
