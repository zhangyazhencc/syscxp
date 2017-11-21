package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointInventory;

import java.util.List;

/**
 * Create by DCY on 2017/11/20
 */
public class APIListInnerEndpointReply extends APIReply {
    private List<InnerConnectedEndpointInventory> inventories;

    public List<InnerConnectedEndpointInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<InnerConnectedEndpointInventory> inventories) {
        this.inventories = inventories;
    }
}
