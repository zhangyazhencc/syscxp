package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/13
 */
public class APIQueryQinqReply extends APIQueryReply {
    private List<QinqInventory> inventories;

    public List<QinqInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<QinqInventory> inventories) {
        this.inventories = inventories;
    }
}
