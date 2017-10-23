package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.tunnel.TunnelForAlarmInventory;

import java.util.List;

public class APIGetResourcesBindByPolicyReply extends APIReply {

    private List<TunnelForAlarmInventory> inventories;

    private long count;

    public List<TunnelForAlarmInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelForAlarmInventory> inventories) {
        this.inventories = inventories;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
