package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnDurationEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnDurationEvent() {
    }

    public APIUpdateVpnDurationEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
