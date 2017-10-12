package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;
import java.util.List;

public class APIQueryAliEdgeRouterReply extends APIQueryReply {
    private List<AliEdgeRouterInventory> inventories;

    public List<AliEdgeRouterInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AliEdgeRouterInventory> inventories) {
        this.inventories = inventories;
    }
}
