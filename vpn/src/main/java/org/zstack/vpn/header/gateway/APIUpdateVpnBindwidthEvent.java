package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnBindwidthEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnBindwidthEvent() {
    }

    public APIUpdateVpnBindwidthEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
