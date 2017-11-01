package com.syscxp.header.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelDetailForAlarmReply extends APIQueryReply {
    private List<TunnelDetailForAlarmInventory> inventories;

    public List<TunnelDetailForAlarmInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelDetailForAlarmInventory> inventories) {
        this.inventories = inventories;
    }
}
