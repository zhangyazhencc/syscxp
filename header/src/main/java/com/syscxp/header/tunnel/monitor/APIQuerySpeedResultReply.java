package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-03.
 * @Description: .
 */
public class APIQuerySpeedResultReply extends APIQueryReply {
    private List<SpeedResultInventory> inventories;

    public List<SpeedResultInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SpeedResultInventory> inventories) {
        this.inventories = inventories;
    }
}
