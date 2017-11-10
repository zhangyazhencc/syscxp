package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
public class APIQueryHostSwitchMonitorReply extends APIQueryReply {
    private List<HostSwitchMonitorInventory> inventories;

    public List<HostSwitchMonitorInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostSwitchMonitorInventory> inventories) {
        this.inventories = inventories;
    }
}
