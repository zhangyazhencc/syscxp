package com.syscxp.header.tunnel.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryZoneReply extends APIQueryReply{

    private List<ZoneInventory> inventories;

    public List<ZoneInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ZoneInventory> inventories) {
        this.inventories = inventories;
    }
}
