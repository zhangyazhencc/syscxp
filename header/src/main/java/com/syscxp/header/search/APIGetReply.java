package com.syscxp.header.search;

import com.syscxp.header.message.APIReply;

public abstract class APIGetReply extends APIReply {
    private String inventory;

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
}
