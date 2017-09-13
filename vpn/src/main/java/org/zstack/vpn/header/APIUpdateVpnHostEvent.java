package org.zstack.vpn.header;

import org.zstack.header.message.APIEvent;

public class APIUpdateVpnHostEvent extends APIEvent{
    VpnHostInventory inventory;

    public APIUpdateVpnHostEvent() {
    }

    public APIUpdateVpnHostEvent(String apiId) {
        super(apiId);
    }

    public VpnHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnHostInventory inventory) {
        this.inventory = inventory;
    }
}
