package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryL3EndPointReply extends APIQueryReply {
    private List<L3EndPointInventory> inventories;

    public List<L3EndPointInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3EndPointInventory> inventories) {
        this.inventories = inventories;
    }

}
