package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

public class APIGetVpnCertReply extends APIReply {
    VpnCertInventory inventory;

    public VpnCertInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnCertInventory inventory) {
        this.inventory = inventory;
    }
}
