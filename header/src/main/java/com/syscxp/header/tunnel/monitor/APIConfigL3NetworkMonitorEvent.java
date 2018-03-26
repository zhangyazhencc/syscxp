package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIConfigL3NetworkMonitorEvent extends APIEvent {
    private L3NetworkMonitorInventory inventory;

    public L3NetworkMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3NetworkMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
