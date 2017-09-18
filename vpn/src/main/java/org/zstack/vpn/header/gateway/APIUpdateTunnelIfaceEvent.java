package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APIUpdateTunnelIfaceEvent extends APIEvent{
    TunnelIfaceInventory inventory;

    public APIUpdateTunnelIfaceEvent() {
    }

    public APIUpdateTunnelIfaceEvent(String apiId) {
        super(apiId);
    }

    public TunnelIfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelIfaceInventory inventory) {
        this.inventory = inventory;
    }
}
