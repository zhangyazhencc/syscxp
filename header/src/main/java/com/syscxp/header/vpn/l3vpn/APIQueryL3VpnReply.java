package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.vpn.vpn.VpnInventory;

import java.util.List;

public class APIQueryL3VpnReply extends APIQueryReply {
    List<L3VpnInventory> inventories;

    public List<L3VpnInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<L3VpnInventory> inventories) {
        this.inventories = inventories;
    }
}
