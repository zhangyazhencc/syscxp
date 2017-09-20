package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelReply extends APIQueryReply {
    private List<TunnelInventory> inventories;

    public List<TunnelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelInventory> inventories) {
        this.inventories = inventories;
    }
}