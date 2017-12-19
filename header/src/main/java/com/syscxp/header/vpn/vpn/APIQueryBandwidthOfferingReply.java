package com.syscxp.header.vpn.vpn;

import com.syscxp.header.configuration.BandwidthOfferingInventory;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryBandwidthOfferingReply extends APIQueryReply {
    private List<BandwidthOfferingInventory> inventories;

    public List<BandwidthOfferingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<BandwidthOfferingInventory> inventories) {
        this.inventories = inventories;
    }
}
