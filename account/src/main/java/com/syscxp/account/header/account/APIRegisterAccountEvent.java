package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;

public class APIRegisterAccountEvent extends APIEvent {
    private AccountInventory inventory;

    public APIRegisterAccountEvent(String apiId) {
        super(apiId);
    }

    public APIRegisterAccountEvent() {
        super(null);
    }

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }
}
