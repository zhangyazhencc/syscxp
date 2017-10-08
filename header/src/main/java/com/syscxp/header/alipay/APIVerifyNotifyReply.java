package com.syscxp.header.alipay;

import com.syscxp.header.message.APIReply;

public class APIVerifyNotifyReply extends APIReply {

    private boolean inventory;

    public boolean getInventory() {
        return inventory;
    }

    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }
}