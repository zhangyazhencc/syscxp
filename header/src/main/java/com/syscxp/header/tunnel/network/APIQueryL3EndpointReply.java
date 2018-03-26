package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryL3EndpointReply extends APIQueryReply {
    private List<L3EndpointInventory> inventories;

    public List<L3EndpointInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3EndpointInventory> inventories) {
        this.inventories = inventories;
    }

}
