package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.configuration.BandwidthOfferingInventory;
import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Create by DCY on 2017/10/30
 */
@RestResponse(fieldsTo = {"inventories"})
public class APIQueryBandwidthOfferingReply extends APIQueryReply {
    private List<BandwidthOfferingInventory> inventories;

    public List<BandwidthOfferingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<BandwidthOfferingInventory> inventories) {
        this.inventories = inventories;
    }
}
