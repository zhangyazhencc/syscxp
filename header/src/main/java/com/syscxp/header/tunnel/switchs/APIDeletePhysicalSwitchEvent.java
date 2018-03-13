package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeletePhysicalSwitchEvent extends APIEvent {
    private PhysicalSwitchInventory inventory;

    public APIDeletePhysicalSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIDeletePhysicalSwitchEvent() {}

    public PhysicalSwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(PhysicalSwitchInventory inventory) {
        this.inventory = inventory;
    }
}
