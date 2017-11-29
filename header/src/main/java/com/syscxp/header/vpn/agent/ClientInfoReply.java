package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class ClientInfoReply extends MessageReply {
    public CertInventory inventory;

    public CertInventory getInventory() {
        return inventory;
    }

    public void setInventory(CertInventory inventory) {
        this.inventory = inventory;
    }
}
