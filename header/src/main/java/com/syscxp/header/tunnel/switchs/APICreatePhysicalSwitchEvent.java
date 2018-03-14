package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-06
 */
public class APICreatePhysicalSwitchEvent extends APIEvent {
    private PhysicalSwitchInventory inventory;

    public APICreatePhysicalSwitchEvent(){}

    public APICreatePhysicalSwitchEvent(String apiId){super(apiId);}

    public PhysicalSwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(PhysicalSwitchInventory inventory) {
        this.inventory = inventory;
    }
}
