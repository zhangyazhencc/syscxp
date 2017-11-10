package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
public class APIQuerySwitchPortReply extends APIQueryReply {
    private List<SwitchPortInventory> inventories;

    public List<SwitchPortInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchPortInventory> inventories) {
        this.inventories = inventories;
    }
}