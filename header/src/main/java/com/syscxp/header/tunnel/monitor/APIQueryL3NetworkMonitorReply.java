package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-4-8.
 * @Description: .
 */
@RestResponse(allTo = "inventories")
public class APIQueryL3NetworkMonitorReply extends APIQueryReply {
    private L3EndpointMonitorInventory inventory;

    public L3EndpointMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
