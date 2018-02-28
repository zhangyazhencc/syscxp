package com.syscxp.header.tunnel.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryZoneNodeRefReply extends APIQueryReply{

    private List<ZoneNodeRefInventory> inventories;

    public List<ZoneNodeRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ZoneNodeRefInventory> inventories) {
        this.inventories = inventories;
    }
}
