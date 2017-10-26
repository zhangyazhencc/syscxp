package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVlanUsedReply extends APIQueryReply {
    private List<VlanUsedInventory> inventories;
    private String count;


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<VlanUsedInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VlanUsedInventory> inventories) {
        this.inventories = inventories;
    }
}
