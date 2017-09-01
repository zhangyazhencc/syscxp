package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-29
 */
public class APIQuerySwitchReply extends APIQueryReply {
    private List<SwitchEndpointSwitchModelInventory> inventories;

    public List<SwitchEndpointSwitchModelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchEndpointSwitchModelInventory> inventories) {
        this.inventories = inventories;
    }
}
