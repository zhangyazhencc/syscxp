package com.syscxp.header.host;

import com.syscxp.header.message.MessageReply;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class ReconnectHostReply extends MessageReply {
    private HostInventory inventory;

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }
}
