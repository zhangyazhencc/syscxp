package com.syscxp.header.host;

import com.syscxp.header.message.APIReply;

public class APIGetHostReply extends APIReply {

    private HostInventory inventory;

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }

    public static APIGetHostReply __example__() {
        APIGetHostReply reply = new APIGetHostReply();


        return reply;
    }

}
