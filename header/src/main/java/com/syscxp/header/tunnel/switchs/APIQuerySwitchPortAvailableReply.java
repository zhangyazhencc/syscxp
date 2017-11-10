package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQuerySwitchPortAvailableReply extends APIQueryReply {

    private List<SwitchPortInventory> inventories;
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<SwitchPortInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchPortInventory> inventories) {
        this.inventories = inventories;
    }
}
