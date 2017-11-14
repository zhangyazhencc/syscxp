package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class APIQueryMonitorResultReply extends APIQueryReply {
    private List<OpenTSDBResultInventory> inventories;
    private OpenTSDBResultInventory inventory;

    public List<OpenTSDBResultInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OpenTSDBResultInventory> inventories) {
        this.inventories = inventories;
    }

    public OpenTSDBResultInventory getInventory() {
        return inventory;
    }

    public void setInventory(OpenTSDBResultInventory inventory) {
        this.inventory = inventory;
    }
}
