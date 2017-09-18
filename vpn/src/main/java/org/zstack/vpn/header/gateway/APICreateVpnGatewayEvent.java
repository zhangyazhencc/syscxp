package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APICreateVpnGatewayEvent extends APIEvent{
    private VpnInventory inventory;

    public APICreateVpnGatewayEvent() {
    }

    public APICreateVpnGatewayEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
