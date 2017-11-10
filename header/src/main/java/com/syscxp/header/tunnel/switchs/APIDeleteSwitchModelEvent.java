package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchModelEvent extends APIEvent {

    private SwitchModelInventory inventory;

    public APIDeleteSwitchModelEvent(String apiId) {super(apiId);}

    public APIDeleteSwitchModelEvent(){}

    public SwitchModelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchModelInventory inventory) {
        this.inventory = inventory;
    }
}
