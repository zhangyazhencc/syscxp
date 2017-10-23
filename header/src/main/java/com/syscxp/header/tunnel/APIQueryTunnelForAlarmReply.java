package com.syscxp.header.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelForAlarmReply extends APIQueryReply {
    private List<TunnelForAlarmInventory> inventories;

    public List<TunnelForAlarmInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelForAlarmInventory> inventories) {
        this.inventories = inventories;
    }
}
