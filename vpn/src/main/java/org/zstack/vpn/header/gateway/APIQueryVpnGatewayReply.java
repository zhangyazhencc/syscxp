package org.zstack.vpn.header.gateway;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnGatewayReply extends APIQueryReply {
    List<VpnInventory> inventories;

    public List<VpnInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnInventory> inventories) {
        this.inventories = inventories;
    }
}
