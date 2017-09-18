package org.zstack.tunnel.header.tunnel;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-09-14
 */
public class APIQueryNetWorkReply extends APIQueryReply {
    private List<NetWorkInventory> inventories;

    public List<NetWorkInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NetWorkInventory> inventories) {
        this.inventories = inventories;
    }
}
