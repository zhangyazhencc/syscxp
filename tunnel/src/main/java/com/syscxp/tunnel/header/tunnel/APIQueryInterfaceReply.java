package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
public class APIQueryInterfaceReply extends APIQueryReply {
    private List<InterfaceInventory> inventories;

    public List<InterfaceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<InterfaceInventory> inventories) {
        this.inventories = inventories;
    }
}
