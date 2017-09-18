package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnGatewayEvent extends APIEvent{
    VpnGatewayInventory inventory;

    public APIUpdateVpnGatewayEvent() {
    }

    public APIUpdateVpnGatewayEvent(String apiId) {
        super(apiId);
    }

    public VpnGatewayInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnGatewayInventory inventory) {
        this.inventory = inventory;
    }
}
