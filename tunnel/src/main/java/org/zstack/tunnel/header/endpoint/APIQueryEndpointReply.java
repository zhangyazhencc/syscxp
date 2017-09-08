package org.zstack.tunnel.header.endpoint;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-23
 */
public class APIQueryEndpointReply extends APIQueryReply {
    private List<EndpointToNodeInventory> inventories;

    public List<EndpointToNodeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<EndpointToNodeInventory> inventories) {
        this.inventories = inventories;
    }
}
