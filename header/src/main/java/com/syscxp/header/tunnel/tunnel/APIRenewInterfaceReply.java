package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIRenewInterfaceReply extends APIReply {

    private InterfaceInventory inventory;

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}