package com.syscxp.vpn.header.host;

import com.syscxp.header.message.APIEvent;

public class APICreateZoneEvent extends APIEvent{
    private ZoneInventory inventory;

    public APICreateZoneEvent() {
    }

    public APICreateZoneEvent(String apiId) {
        super(apiId);
    }

    public ZoneInventory getInventory() {
        return inventory;
    }

    public void setInventory(ZoneInventory inventory) {
        this.inventory = inventory;
    }
}
