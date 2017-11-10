package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryAliUserReply extends APIQueryReply {
    private List<AliUserInventory> inventories;

    public List<AliUserInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AliUserInventory> inventories) {
        this.inventories = inventories;
    }
}
