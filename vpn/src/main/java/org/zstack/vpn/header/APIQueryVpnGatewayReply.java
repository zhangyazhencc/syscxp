package org.zstack.vpn.header;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryVpnGatewayReply extends APIQueryReply {
    List<VpnGatewayInventory> inventories;

    public List<VpnGatewayInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnGatewayInventory> inventories) {
        this.inventories = inventories;
    }
}
