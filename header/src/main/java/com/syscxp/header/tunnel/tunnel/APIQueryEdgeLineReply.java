package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2018/1/11
 */
public class APIQueryEdgeLineReply extends APIQueryReply {
    private List<EdgeLineInventory> inventories;

    public List<EdgeLineInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<EdgeLineInventory> inventories) {
        this.inventories = inventories;
    }
}
