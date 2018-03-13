package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchEvent extends APIEvent {
    private SwitchInventory inventory;

    public APIDeleteSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSwitchEvent() {}

    public SwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchInventory inventory) {
        this.inventory = inventory;
    }
}
