package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-02.
 * @Description: .
 */

public class APIQueryL3NetworkMonitorDetailReply extends APIReply {
    private L3NetworkMonitorDetailInventory inventory;
    private List<L3NetworkMonitorDetailInventory> inventories;

    public L3NetworkMonitorDetailInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3NetworkMonitorDetailInventory inventory) {
        this.inventory = inventory;
    }

    public List<L3NetworkMonitorDetailInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3NetworkMonitorDetailInventory> inventories) {
        this.inventories = inventories;
    }
}
