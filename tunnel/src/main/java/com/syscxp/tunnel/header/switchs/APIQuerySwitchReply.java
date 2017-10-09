package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-29
 */
public class APIQuerySwitchReply extends APIQueryReply {
    private List<SwitchInventory> inventories;

    public List<SwitchInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchInventory> inventories) {
        this.inventories = inventories;
    }
}
