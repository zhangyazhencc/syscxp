package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIRenewInterfaceEvent extends APIEvent {

    private InterfaceInventory inventory;

    public APIRenewInterfaceEvent(){}

    public APIRenewInterfaceEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
