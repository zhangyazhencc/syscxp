package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetMonitorTargeListReply extends APIReply {

    private List<MonitorTargetInventory> inventories;

    public List<MonitorTargetInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<MonitorTargetInventory> inventories) {
        this.inventories = inventories;
    }
}
