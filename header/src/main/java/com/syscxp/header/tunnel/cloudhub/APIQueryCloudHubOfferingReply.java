package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryCloudHubOfferingReply extends APIQueryReply {

    private List<CloudHubOfferingInventory> inventories;

    public List<CloudHubOfferingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<CloudHubOfferingInventory> inventories) {
        this.inventories = inventories;
    }
}
