package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2018/3/28
 */
public class APIQueryOutsideResourceReply extends APIQueryReply {
    private List<OutsideResourceInventory> inventories;

    public List<OutsideResourceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OutsideResourceInventory> inventories) {
        this.inventories = inventories;
    }
}
