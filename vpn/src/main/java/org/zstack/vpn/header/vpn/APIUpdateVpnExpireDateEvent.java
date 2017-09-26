package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnExpireDateEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnExpireDateEvent() {
    }

    public APIUpdateVpnExpireDateEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
