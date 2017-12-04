package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.vpn.VpnCertInventory;

public class ClientInfoReply extends MessageReply {
    private VpnCertInventory inventory;

    public VpnCertInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnCertInventory inventory) {
        this.inventory = inventory;
    }
}
