package com.syscxp.vpn.header.vpn;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnReply extends APIQueryReply {
    List<VpnInventory> inventories;

    public List<VpnInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnInventory> inventories) {
        this.inventories = inventories;
    }
}
