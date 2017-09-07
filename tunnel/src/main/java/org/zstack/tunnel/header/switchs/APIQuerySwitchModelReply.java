package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-06
 */
public class APIQuerySwitchModelReply extends APIQueryReply {
    private List<SwitchModelInventory> inventories;

    public List<SwitchModelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchModelInventory> inventories) {
        this.inventories = inventories;
    }
}
