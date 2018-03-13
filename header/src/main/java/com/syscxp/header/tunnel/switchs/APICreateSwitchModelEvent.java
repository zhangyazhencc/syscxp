package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-06
 */
public class APICreateSwitchModelEvent extends APIEvent {
    private SwitchModelInventory inventory;

    public APICreateSwitchModelEvent(){}

    public APICreateSwitchModelEvent(String apiId){super(apiId);}

    public SwitchModelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchModelInventory inventory) {
        this.inventory = inventory;
    }
}
