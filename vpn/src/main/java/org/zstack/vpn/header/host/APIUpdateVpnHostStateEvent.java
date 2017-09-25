package org.zstack.vpn.header.host;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnHostStateEvent extends APIEvent{
    VpnHostInventory inventory;

    public APIUpdateVpnHostStateEvent() {
    }

    public APIUpdateVpnHostStateEvent(String apiId) {
        super(apiId);
    }

    public VpnHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnHostInventory inventory) {
        this.inventory = inventory;
    }
}
