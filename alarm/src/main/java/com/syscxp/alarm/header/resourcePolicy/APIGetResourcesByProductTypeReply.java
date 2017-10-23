package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.tunnel.TunnelForAlarmInventory;

import java.util.List;

public class APIGetResourcesByProductTypeReply extends APIReply{
    List<TunnelForAlarmInventory> inventories;

    public List<TunnelForAlarmInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelForAlarmInventory> inventories) {
        this.inventories = inventories;
    }
}
