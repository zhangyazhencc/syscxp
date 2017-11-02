package com.syscxp.tunnel.header.endpoint;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/2
 */
public class APIQueryInnerEndpointReply extends APIQueryReply {
    private List<InnerConnectedEndpointInventory> inventories;

    public List<InnerConnectedEndpointInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<InnerConnectedEndpointInventory> inventories) {
        this.inventories = inventories;
    }
}
