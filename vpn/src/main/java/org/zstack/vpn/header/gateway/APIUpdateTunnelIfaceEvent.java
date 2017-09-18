package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APIUpdateTunnelIfaceEvent extends APIEvent{
    VpnInterfaceInventory inventory;

    public APIUpdateTunnelIfaceEvent() {
    }

    public APIUpdateTunnelIfaceEvent(String apiId) {
        super(apiId);
    }

    public VpnInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
