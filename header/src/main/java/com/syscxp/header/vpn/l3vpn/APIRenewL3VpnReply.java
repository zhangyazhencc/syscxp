package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIReply;

public class APIRenewL3VpnReply extends APIReply {

    private L3VpnInventory inventory;

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
