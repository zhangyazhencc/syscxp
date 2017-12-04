package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVlanUsedReply extends APIQueryReply {
    private List<VlanUsedInventory> inventories;

    public List<VlanUsedInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VlanUsedInventory> inventories) {
        this.inventories = inventories;
    }
}
