package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-29
 */
public class APICreateSwitchEvent extends APIEvent {
    private SwitchInventory inventory;

    public APICreateSwitchEvent(){}

    public APICreateSwitchEvent(String apiId){super(apiId);}

    public SwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchInventory inventory) {
        this.inventory = inventory;
    }
}
