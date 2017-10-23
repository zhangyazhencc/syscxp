package com.syscxp.header.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelForBillingReply extends APIQueryReply {
    private List<TunnelForBillingInventory> inventories;

    public List<TunnelForBillingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelForBillingInventory> inventories) {
        this.inventories = inventories;
    }
}
