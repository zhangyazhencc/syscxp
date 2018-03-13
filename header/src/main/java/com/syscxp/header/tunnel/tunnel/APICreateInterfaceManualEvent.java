package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-11
 */
public class APICreateInterfaceManualEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APICreateInterfaceManualEvent(){}

    public APICreateInterfaceManualEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
