package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQuerySwitchPortUsedReply extends APIQueryReply {
    private List<SwitchPortUsedInventory> inventories;

    public List<SwitchPortUsedInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchPortUsedInventory> inventories) {
        this.inventories = inventories;
    }
}
