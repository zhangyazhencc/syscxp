package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;

public class APIUpdateAccountEvent extends APIEvent {
    private AccountInventory inventory;

    public APIUpdateAccountEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateAccountEvent() {
        super(null);
    }

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }

}
