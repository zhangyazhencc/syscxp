package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APICreateTunnelIfaceEvent extends APIEvent{
    private VpnInterfaceInventory inventory;

    public APICreateTunnelIfaceEvent() {
    }

    public APICreateTunnelIfaceEvent(String apiId) {
        super(apiId);
    }

    public VpnInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
