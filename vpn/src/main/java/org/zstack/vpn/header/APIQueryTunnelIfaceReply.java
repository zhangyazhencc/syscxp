package org.zstack.vpn.header;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryTunnelIfaceReply extends APIQueryReply {
    List<TunnelIfaceInventory> inventories;

    public List<TunnelIfaceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelIfaceInventory> inventories) {
        this.inventories = inventories;
    }
}
