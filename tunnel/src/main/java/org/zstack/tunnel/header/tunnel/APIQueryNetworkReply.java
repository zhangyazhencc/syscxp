package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-14
 */
public class APIQueryNetworkReply extends APIQueryReply {
    private List<NetworkInventory> inventories;

    public List<NetworkInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NetworkInventory> inventories) {
        this.inventories = inventories;
    }
}
