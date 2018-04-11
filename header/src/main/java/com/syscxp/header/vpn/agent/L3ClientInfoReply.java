package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.l3vpn.L3VpnInventory;

public class L3ClientInfoReply extends MessageReply {
    private L3VpnInventory inventory;

    public L3VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3VpnInventory inventory) {
        this.inventory = inventory;
    }
}
