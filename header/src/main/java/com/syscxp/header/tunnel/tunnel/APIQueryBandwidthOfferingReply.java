package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2017/10/30
 */
public class APIQueryBandwidthOfferingReply extends APIQueryReply {
    private List<BandwidthOfferingInventory> inventories;

    public List<BandwidthOfferingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<BandwidthOfferingInventory> inventories) {
        this.inventories = inventories;
    }
}
