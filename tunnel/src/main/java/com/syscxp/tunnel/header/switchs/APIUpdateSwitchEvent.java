package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-29
 */
@RestResponse(allTo = "inventory")
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
