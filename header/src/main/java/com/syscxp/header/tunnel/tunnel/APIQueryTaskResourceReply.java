package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/8
 */
public class APIQueryTaskResourceReply extends APIQueryReply {
    private List<TaskResourceInventory> inventories;

    public List<TaskResourceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TaskResourceInventory> inventories) {
        this.inventories = inventories;
    }
}
