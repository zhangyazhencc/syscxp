package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryPhysicalSwitchUpLinkRefReply extends APIQueryReply {
    private List<PhysicalSwitchUpLinkRefInventory> inventories;

    public List<PhysicalSwitchUpLinkRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PhysicalSwitchUpLinkRefInventory> inventories) {
        this.inventories = inventories;
    }
}
