package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2017/10/30
 */
public class APIQueryPortOfferingReply extends APIQueryReply {
    private List<PortOfferingInventory> inventories;

    public List<PortOfferingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PortOfferingInventory> inventories) {
        this.inventories = inventories;
    }
}
