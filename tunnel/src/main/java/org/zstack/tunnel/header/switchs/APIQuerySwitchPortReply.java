package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
public class APIQuerySwitchPortReply extends APIQueryReply {
    private List<SwitchPortToModelInventory> inventories;

    public List<SwitchPortToModelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchPortToModelInventory> inventories) {
        this.inventories = inventories;
    }
}
