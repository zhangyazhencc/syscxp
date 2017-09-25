package org.zstack.vpn.header.host;

import org.zstack.header.message.APIEvent;

public class APICreateHostInterfaceEvent extends APIEvent{
    private HostInterfaceInventory inventory;

    public APICreateHostInterfaceEvent() {
    }

    public APICreateHostInterfaceEvent(String apiId) {
        super(apiId);
    }

    public HostInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
