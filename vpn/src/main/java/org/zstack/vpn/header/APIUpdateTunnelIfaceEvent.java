package org.zstack.vpn.header;

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
