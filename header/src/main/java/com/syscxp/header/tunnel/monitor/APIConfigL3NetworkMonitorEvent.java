package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIConfigL3NetworkMonitorEvent extends APIEvent {
    private L3EndpointMonitorInventory inventory;

    public L3EndpointMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
