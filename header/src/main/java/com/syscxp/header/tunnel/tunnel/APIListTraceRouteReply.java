package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
public class APIListTraceRouteReply extends APIReply {
    private List<TraceRouteInventory> inventories;

    public List<TraceRouteInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TraceRouteInventory> inventories) {
        this.inventories = inventories;
    }
}
