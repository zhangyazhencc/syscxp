package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.tunnel.TunnelForAlarmInventory;

import java.util.List;

public class APIGetResourcesByProductTypeReply extends APIReply{
    private List<ResourceInventory> inventories;

    private long count;

    public List<ResourceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourceInventory> inventories) {
        this.inventories = inventories;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
