package org.zstack.vpn.header.gateway;

import org.zstack.header.message.APIEvent;

public class APICreateTunnelIfaceEvent extends APIEvent{
    private TunnelIfaceInventory inventory;

    public APICreateTunnelIfaceEvent() {
    }

    public APICreateTunnelIfaceEvent(String apiId) {
        super(apiId);
    }

    public TunnelIfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelIfaceInventory inventory) {
        this.inventory = inventory;
    }
}
