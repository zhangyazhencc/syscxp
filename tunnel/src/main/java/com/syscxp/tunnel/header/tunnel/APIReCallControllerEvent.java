package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/9
 */
@RestResponse(allTo = "inventory")
public class APIReCallControllerEvent extends APIEvent {
    private TaskResourceInventory inventory;

    public APIReCallControllerEvent(){}

    public APIReCallControllerEvent(String apiId){super(apiId);}

    public TaskResourceInventory getInventory() {
        return inventory;
    }

    public void setInventory(TaskResourceInventory inventory) {
        this.inventory = inventory;
    }
}
