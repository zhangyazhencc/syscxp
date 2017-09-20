package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIReply;

public class APIGetVpnReply extends APIReply {
    VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
