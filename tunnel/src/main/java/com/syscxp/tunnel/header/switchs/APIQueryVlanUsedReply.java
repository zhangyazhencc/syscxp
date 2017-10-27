package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVlanUsedReply extends APIQueryReply {
    private List<VlanUsedInventory> inventories;
    private Integer count;


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<VlanUsedInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VlanUsedInventory> inventories) {
        this.inventories = inventories;
    }
}
