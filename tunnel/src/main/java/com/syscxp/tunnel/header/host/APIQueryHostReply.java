package com.syscxp.tunnel.header.host;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-30
 */
public class APIQueryHostReply extends APIQueryReply {
    private List<HostInventory> inventories;

    public List<HostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostInventory> inventories) {
        this.inventories = inventories;
    }
}
