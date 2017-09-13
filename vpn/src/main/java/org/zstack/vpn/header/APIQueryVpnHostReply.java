package org.zstack.vpn.header;

import org.zstack.header.query.APIQueryReply;

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
