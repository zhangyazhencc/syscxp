package com.syscxp.account.header.identity;

import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIReply;

public class APILogInReply extends APIReply {
    private SessionInventory inventory;

    public SessionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SessionInventory inventory) {
        this.inventory = inventory;
    }
}
