package com.syscxp.header.vpn.vpn;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnSystemReply extends APIQueryReply {
    List<VpnInventory> inventories;

    public List<VpnInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnInventory> inventories) {
        this.inventories = inventories;
    }
}
