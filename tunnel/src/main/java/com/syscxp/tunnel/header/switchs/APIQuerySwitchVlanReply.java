package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
public class APIQuerySwitchVlanReply extends APIQueryReply {
    private List<SwitchVlanInventory> inventories;

    public List<SwitchVlanInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchVlanInventory> inventories) {
        this.inventories = inventories;
    }
}
