package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class APIQueryNettoolResultReply extends APIQueryReply {
    private List<NettoolResultInventory> inventories;
    private NettoolResultInventory inventory;

    public List<NettoolResultInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NettoolResultInventory> inventories) {
        this.inventories = inventories;
    }

    public NettoolResultInventory getInventory() {
        return inventory;
    }

    public void setInventory(NettoolResultInventory inventory) {
        this.inventory = inventory;
    }
}
