package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2018/3/28
 */
@RestResponse(allTo = "inventory")
public class APICreateOutsideResourceEvent extends APIEvent {
    private OutsideResourceInventory inventory;

    public APICreateOutsideResourceEvent(){}

    public APICreateOutsideResourceEvent(String apiId){super(apiId);}

    public OutsideResourceInventory getInventory() {
        return inventory;
    }

    public void setInventory(OutsideResourceInventory inventory) {
        this.inventory = inventory;
    }
}
