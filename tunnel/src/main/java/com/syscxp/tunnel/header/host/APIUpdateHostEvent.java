package com.syscxp.tunnel.header.host;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-30
 */
@RestResponse(allTo = "inventory")
public class APIUpdateHostEvent extends APIEvent {

    private HostInventory inventory;

    public APIUpdateHostEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateHostEvent() {}

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }
}
