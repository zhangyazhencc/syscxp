package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

public class APIRenewAutoVpnReply extends APIReply {

    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
