package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APICreateVpnInterfaceEvent extends APIEvent{
    private VpnInterfaceInventory inventory;

    public APICreateVpnInterfaceEvent() {
    }

    public APICreateVpnInterfaceEvent(String apiId) {
        super(apiId);
    }

    public VpnInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
