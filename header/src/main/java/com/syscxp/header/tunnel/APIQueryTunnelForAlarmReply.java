package com.syscxp.header.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelForAlarmReply extends APIReply {
    private List<TunnelForAlarmInventory> inventories;

    private Long count;

    public List<TunnelForAlarmInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelForAlarmInventory> inventories) {
        this.inventories = inventories;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
