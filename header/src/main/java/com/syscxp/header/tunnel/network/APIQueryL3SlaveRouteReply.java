package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2018/4/19
 */
public class APIQueryL3SlaveRouteReply extends APIQueryReply {
    private List<L3SlaveRouteInventory> inventories;

    public List<L3SlaveRouteInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3SlaveRouteInventory> inventories) {
        this.inventories = inventories;
    }
}
