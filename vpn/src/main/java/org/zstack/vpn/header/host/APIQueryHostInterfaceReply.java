package org.zstack.vpn.header.host;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryHostInterfaceReply extends APIQueryReply {
    List<HostInterfaceInventory> inventories;

    public List<HostInterfaceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostInterfaceInventory> inventories) {
        this.inventories = inventories;
    }
}
