package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class TunnelQueryReply extends APIQueryReply {
    private List<TunnelQueryInventory> inventory;

    public List<TunnelQueryInventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<TunnelQueryInventory> inventory) {
        this.inventory = inventory;
    }
}
