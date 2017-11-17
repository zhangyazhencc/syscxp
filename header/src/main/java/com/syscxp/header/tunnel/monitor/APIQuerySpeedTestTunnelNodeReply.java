package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */

public class APIQuerySpeedTestTunnelNodeReply extends APIQueryReply {
    private List<SpeedTestTunnelNodeInventory> inventories;

    public List<SpeedTestTunnelNodeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SpeedTestTunnelNodeInventory> inventories) {
        this.inventories = inventories;
    }
}
