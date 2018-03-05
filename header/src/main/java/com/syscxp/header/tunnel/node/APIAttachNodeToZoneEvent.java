package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

public class APIAttachNodeToZoneEvent extends APIEvent{

    private ZoneNodeRefInventory inventory;

    public APIAttachNodeToZoneEvent(String apiId) {
        super(apiId);
    }

    public ZoneNodeRefInventory getInventory() {
        return inventory;
    }

    public void setInventory(ZoneNodeRefInventory inventory) {
        this.inventory = inventory;
    }

    public APIAttachNodeToZoneEvent() {
    }
}
