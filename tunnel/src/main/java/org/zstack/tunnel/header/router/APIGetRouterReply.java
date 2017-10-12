package org.zstack.tunnel.header.router;

import org.zstack.header.message.APIReply;

public class APIGetRouterReply extends APIReply {

    private RouterInventory inventory;

    public RouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(RouterInventory inventory) {
        this.inventory = inventory;
    }
}
