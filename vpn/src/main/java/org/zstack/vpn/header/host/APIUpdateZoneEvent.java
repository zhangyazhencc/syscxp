package org.zstack.vpn.header.host;

import org.zstack.header.message.APIEvent;

public class APIUpdateZoneEvent extends APIEvent{
    ZoneInventory inventory;

    public APIUpdateZoneEvent() {
    }

    public APIUpdateZoneEvent(String apiId) {
        super(apiId);
    }

    public ZoneInventory getInventory() {
        return inventory;
    }

    public void setInventory(ZoneInventory inventory) {
        this.inventory = inventory;
    }
}
