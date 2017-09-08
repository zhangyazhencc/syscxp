package org.zstack.tunnel.header.switchs;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-29
 */
public class APIQuerySwitchReply extends APIQueryReply {
    private List<SwitchToEndpointAndModelInventory> inventories;

    public List<SwitchToEndpointAndModelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SwitchToEndpointAndModelInventory> inventories) {
        this.inventories = inventories;
    }
}
