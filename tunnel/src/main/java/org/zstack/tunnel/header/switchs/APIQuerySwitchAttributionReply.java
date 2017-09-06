package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-06
 */
public class APIQuerySwitchAttributionReply extends APIQueryReply {
    private List<SwitchAttributionInventory> inventories;

    public List<SwitchAttributionInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchAttributionInventory> inventories) {
        this.inventories = inventories;
    }
}
