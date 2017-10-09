package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
public class APIQueryTunnelMonitorReply extends APIQueryReply {
    private List<TunnelMonitorInventory> inventories;

    public List<TunnelMonitorInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelMonitorInventory> inventories) {
        this.inventories = inventories;
    }
}
