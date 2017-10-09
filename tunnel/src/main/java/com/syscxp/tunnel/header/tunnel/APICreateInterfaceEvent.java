package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-08
 */
@RestResponse(allTo = "inventory")
public class APICreateInterfaceEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APICreateInterfaceEvent(){}

    public APICreateInterfaceEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
