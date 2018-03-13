package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

public class APISLAVpnReply extends APIReply {

    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
