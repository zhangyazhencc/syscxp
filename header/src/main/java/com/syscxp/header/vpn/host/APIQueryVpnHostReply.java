package com.syscxp.header.vpn.host;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnHostReply extends APIQueryReply {
    private List<VpnHostInventory> inventories;

    public List<VpnHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnHostInventory> inventories) {
        this.inventories = inventories;
    }

}
