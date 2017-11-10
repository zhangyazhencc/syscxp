package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-14
 */
@RestResponse(allTo = "inventory")
public class APIUpdateInterfaceEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APIUpdateInterfaceEvent(){}

    public APIUpdateInterfaceEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
