package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListAliTunnelReply extends APIQueryReply {
    private List<AliTunnelInventory> inventory;

    public List<AliTunnelInventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<AliTunnelInventory> inventory) {
        this.inventory = inventory;
    }
}
