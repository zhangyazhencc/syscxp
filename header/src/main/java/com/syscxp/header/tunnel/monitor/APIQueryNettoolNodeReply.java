package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.tunnel.host.MonitorHostInventory;
import com.syscxp.header.tunnel.node.NodeInventory;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */

public class APIQueryNettoolNodeReply extends APIQueryReply {
    private List<MonitorHostInventory> inventories;

    public List<MonitorHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<MonitorHostInventory> inventories) {
        this.inventories = inventories;
    }
}
