package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchVlanEvent extends APIEvent {
    private SwitchVlanInventory inventory;

    public APIDeleteSwitchVlanEvent(String apiId) {super(apiId);}

    public APIDeleteSwitchVlanEvent(){}

    public SwitchVlanInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchVlanInventory inventory) {
        this.inventory = inventory;
    }
}
