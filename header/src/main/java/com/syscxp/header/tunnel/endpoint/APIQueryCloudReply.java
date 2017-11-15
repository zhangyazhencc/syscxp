package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-23
 */
public class APIQueryCloudReply extends APIQueryReply {
    private List<CloudInventory> inventories;

    public List<CloudInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<CloudInventory> inventories) {
        this.inventories = inventories;
    }
}
