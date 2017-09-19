package org.zstack.vpn.header.vpn;

import org.zstack.header.query.APIQueryReply;

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
