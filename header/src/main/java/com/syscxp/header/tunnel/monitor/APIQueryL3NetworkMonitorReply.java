package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-4-8.
 * @Description: .
 */
public class APIQueryL3NetworkMonitorReply extends APIQueryReply {
    private L3NetworkMonitorInventory inventory;

    private List<L3NetworkMonitorInventory> inventories;
    
    public L3NetworkMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3NetworkMonitorInventory inventory) {
        this.inventory = inventory;
    }

    public List<L3NetworkMonitorInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3NetworkMonitorInventory> inventories) {
        this.inventories = inventories;
    }
}
