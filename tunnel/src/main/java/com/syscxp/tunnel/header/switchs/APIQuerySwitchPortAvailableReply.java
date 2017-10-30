package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQuerySwitchPortAvailableReply extends APIQueryReply {

    private List<SwitchPortAvailableInventory> inventories;
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<SwitchPortAvailableInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchPortAvailableInventory> inventories) {
        this.inventories = inventories;
    }
}
