package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-08-29
 */
public class APIUpdateSwitchEvent extends APIEvent {
    private SwitchInventory inventory;

    public APIUpdateSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSwitchEvent() {}

    public SwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchInventory inventory) {
        this.inventory = inventory;
    }
}
