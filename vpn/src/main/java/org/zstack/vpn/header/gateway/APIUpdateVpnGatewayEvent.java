package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnGatewayEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnGatewayEvent() {
    }

    public APIUpdateVpnGatewayEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
