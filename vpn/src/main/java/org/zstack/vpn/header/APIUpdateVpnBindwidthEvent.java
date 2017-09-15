package org.zstack.vpn.header;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnBindwidthEvent extends APIEvent{
    VpnGatewayInventory inventory;

    public APIUpdateVpnBindwidthEvent() {
    }

    public APIUpdateVpnBindwidthEvent(String apiId) {
        super(apiId);
    }

    public VpnGatewayInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnGatewayInventory inventory) {
        this.inventory = inventory;
    }
}
