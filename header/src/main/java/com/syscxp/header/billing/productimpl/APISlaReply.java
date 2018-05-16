package com.syscxp.header.billing.productimpl;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.vpn.vpn.VpnInventory;

public class APISlaReply extends APIReply {

    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
