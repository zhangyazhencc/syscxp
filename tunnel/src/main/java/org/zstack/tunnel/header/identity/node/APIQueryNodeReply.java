package org.zstack.tunnel.header.identity.node;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-21
 */
public class APIQueryNodeReply extends APIQueryReply {
    private List<NodeInventory> inventories;

    public List<NodeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NodeInventory> inventories) {
        this.inventories = inventories;
    }
}
