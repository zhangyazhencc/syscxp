package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryCloudHubTunnelRefReply extends APIQueryReply {

    private List<CloudHubTunnelRefInventory> inventories;

    public List<CloudHubTunnelRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<CloudHubTunnelRefInventory> inventories) {
        this.inventories = inventories;
    }
}
