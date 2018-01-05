package com.syscxp.header.billing;

import com.syscxp.header.message.APIEvent;

public class APIDeleteExpiredRenewEvent extends APIEvent {

    private boolean inventory;

    public APIDeleteExpiredRenewEvent() {
    }

    public APIDeleteExpiredRenewEvent(String apiId) {
        super(apiId);
    }

    public boolean isInventory() {
        return inventory;
    }

    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }
}
