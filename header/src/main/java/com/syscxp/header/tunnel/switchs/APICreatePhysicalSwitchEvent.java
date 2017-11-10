package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-06
 */
@RestResponse(allTo = "inventory")
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
