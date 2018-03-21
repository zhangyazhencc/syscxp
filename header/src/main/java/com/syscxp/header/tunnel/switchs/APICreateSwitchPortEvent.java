package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateSwitchPortEvent extends APIEvent {
    private SwitchPortInventory inventory;

    public APICreateSwitchPortEvent(){}

    public APICreateSwitchPortEvent(String apiId){super(apiId);}

    public SwitchPortInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchPortInventory inventory) {
        this.inventory = inventory;
    }
}
