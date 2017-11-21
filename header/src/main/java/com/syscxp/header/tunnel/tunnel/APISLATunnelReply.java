package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2017/11/15
 */
public class APISLATunnelReply extends APIReply {
    private TunnelInventory inventory;

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
