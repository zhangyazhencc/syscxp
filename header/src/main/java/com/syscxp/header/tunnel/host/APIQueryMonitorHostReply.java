package com.syscxp.header.tunnel.host;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryMonitorHostReply extends APIQueryReply {
    private List<MonitorHostInventory> inventories;

    public List<MonitorHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<MonitorHostInventory> inventories) {
        this.inventories = inventories;
    }

}
