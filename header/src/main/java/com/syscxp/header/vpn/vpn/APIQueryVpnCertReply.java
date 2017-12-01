package com.syscxp.header.vpn.vpn;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnCertReply extends APIQueryReply {
    List<VpnCertInventory> inventories;

    public List<VpnCertInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnCertInventory> inventories) {
        this.inventories = inventories;
    }
}
