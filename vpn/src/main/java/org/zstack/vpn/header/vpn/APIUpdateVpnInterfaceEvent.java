package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnInterfaceEvent extends APIEvent{
    VpnInterfaceInventory inventory;

    public APIUpdateVpnInterfaceEvent() {
    }

    public APIUpdateVpnInterfaceEvent(String apiId) {
        super(apiId);
    }

    public VpnInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
