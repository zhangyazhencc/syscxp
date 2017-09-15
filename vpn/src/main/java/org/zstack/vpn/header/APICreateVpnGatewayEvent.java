package org.zstack.vpn.header;

import org.zstack.header.message.APIEvent;

public class APICreateVpnGatewayEvent extends APIEvent{
    private VpnGatewayInventory inventory;

    public APICreateVpnGatewayEvent() {
    }

    public APICreateVpnGatewayEvent(String apiId) {
        super(apiId);
    }

    public VpnGatewayInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnGatewayInventory inventory) {
        this.inventory = inventory;
    }
}
