package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2017/9/28
 */
public class APIUpdateInterfacePortEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APIUpdateInterfacePortEvent(){}

    public APIUpdateInterfacePortEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
