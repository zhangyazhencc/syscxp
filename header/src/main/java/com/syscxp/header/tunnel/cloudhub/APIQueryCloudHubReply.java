package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryCloudHubReply extends APIQueryReply {

    List<CloudHubInventory> inventories;

    public List<CloudHubInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<CloudHubInventory> inventories) {
        this.inventories = inventories;
    }
}
