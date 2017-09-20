package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnStateEvent extends APIEvent{
    VpnInventory inventory;

    public APIUpdateVpnStateEvent() {
    }

    public APIUpdateVpnStateEvent(String apiId) {
        super(apiId);
    }

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}