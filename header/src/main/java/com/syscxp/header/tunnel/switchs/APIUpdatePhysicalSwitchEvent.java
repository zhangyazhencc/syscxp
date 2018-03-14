package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-06
 */
public class APIUpdatePhysicalSwitchEvent extends APIEvent {
    private PhysicalSwitchInventory inventory;

    public APIUpdatePhysicalSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIUpdatePhysicalSwitchEvent() {}

    public PhysicalSwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(PhysicalSwitchInventory inventory) {
        this.inventory = inventory;
    }
}
