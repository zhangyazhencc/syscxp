package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2018/1/12
 */
public class APISLAEdgeLineReply extends APIReply {
    private EdgeLineInventory inventory;

    public EdgeLineInventory getInventory() {
        return inventory;
    }

    public void setInventory(EdgeLineInventory inventory) {
        this.inventory = inventory;
    }
}
