package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;

public class APIConfigL3NetworkMonitorEvent extends APIEvent {
    public APIConfigL3NetworkMonitorEvent() {

    }

    public APIConfigL3NetworkMonitorEvent(String apiId) {
        super(apiId);
    }

    private L3EndpointMonitorInventory inventory;

    public L3EndpointMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3EndpointMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
