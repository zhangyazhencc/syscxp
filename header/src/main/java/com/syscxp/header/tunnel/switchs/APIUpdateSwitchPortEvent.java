package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-13
 */
@RestResponse(allTo = "inventory")
public class APIUpdateSwitchPortEvent extends APIEvent {
    private SwitchPortInventory inventory;

    public APIUpdateSwitchPortEvent(){}

    public APIUpdateSwitchPortEvent(String apiId){super(apiId);}

    public SwitchPortInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchPortInventory inventory) {
        this.inventory = inventory;
    }
}
