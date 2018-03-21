package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

public class APIQueryL3RouteReply extends APIQueryReply {
    private List<L3RouteInventory> inventories;

    public List<L3RouteInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3RouteInventory> inventories) {
        this.inventories = inventories;
    }

}
