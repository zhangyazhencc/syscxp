package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/10/11
 */
@RestResponse(allTo = "inventory")
public class APICreateInnerVlanEvent extends APIEvent {
    private QinqInventory inventory;

    public APICreateInnerVlanEvent(){}

    public APICreateInnerVlanEvent(String apiId){super(apiId);}

    public QinqInventory getInventory() {
        return inventory;
    }

    public void setInventory(QinqInventory inventory) {
        this.inventory = inventory;
    }
}
