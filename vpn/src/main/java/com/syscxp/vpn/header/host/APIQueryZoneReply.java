package com.syscxp.vpn.header.host;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryZoneReply extends APIQueryReply {
    List<ZoneInventory> inventories;

    public List<ZoneInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ZoneInventory> inventories) {
        this.inventories = inventories;
    }
}
