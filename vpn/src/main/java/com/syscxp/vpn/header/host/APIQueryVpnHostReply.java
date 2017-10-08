package com.syscxp.vpn.header.host;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnHostReply extends APIQueryReply {
    List<VpnHostInventory> inventories;

    public List<VpnHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnHostInventory> inventories) {
        this.inventories = inventories;
    }
}
