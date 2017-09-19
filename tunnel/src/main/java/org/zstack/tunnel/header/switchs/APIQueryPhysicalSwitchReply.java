package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-06
 */
public class APIQueryPhysicalSwitchReply extends APIQueryReply {
    private List<PhysicalSwitchInventory> inventories;

    public List<PhysicalSwitchInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PhysicalSwitchInventory> inventories) {
        this.inventories = inventories;
    }
}
