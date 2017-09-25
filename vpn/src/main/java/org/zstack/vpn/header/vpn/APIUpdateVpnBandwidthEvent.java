package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnBandwidthEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnBandwidthEvent() {
    }

    public APIUpdateVpnBandwidthEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
