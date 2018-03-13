package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateSwitchVlanEvent extends APIEvent {
    private SwitchVlanInventory inventory;

    public APICreateSwitchVlanEvent(){}

    public APICreateSwitchVlanEvent(String apiId){super(apiId);}

    public SwitchVlanInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchVlanInventory inventory) {
        this.inventory = inventory;
    }
}
