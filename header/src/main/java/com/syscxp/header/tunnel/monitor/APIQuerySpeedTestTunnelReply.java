package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */

public class APIQuerySpeedTestTunnelReply extends APIQueryReply {
    private List<SpeedRecordsInventory> inventories;

    public List<SpeedRecordsInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SpeedRecordsInventory> inventories) {
        this.inventories = inventories;
    }
}
