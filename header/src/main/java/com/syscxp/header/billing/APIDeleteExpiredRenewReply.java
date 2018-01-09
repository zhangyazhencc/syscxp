package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

public class APIDeleteExpiredRenewReply extends APIReply {

    private boolean inventory;

    public boolean isInventory() {
        return inventory;
    }

    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }
}
