package com.syscxp.account.header.user;

import com.syscxp.header.message.APIEvent;

public class APIUserPWDBackEvent extends APIEvent {

    private UserInventory inventory;

    public UserInventory getInventory() {
        return inventory;
    }

    public void setInventory(UserInventory inventory) {
        this.inventory = inventory;
    }

    public APIUserPWDBackEvent(String apiId) {
        super(apiId);
    }

    public APIUserPWDBackEvent() {
        super(null);
    }

}
