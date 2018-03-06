package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-05.
 * @Description: .
 */
public class APIQueryOpentsdbConditionReply extends APIQueryReply {
    private TunnelConditionInventory inventory;
    private List<TunnelConditionInventory> inventories;

    public TunnelConditionInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelConditionInventory inventory) {
        this.inventory = inventory;
    }

    public List<TunnelConditionInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelConditionInventory> inventories) {
        this.inventories = inventories;
    }
}
