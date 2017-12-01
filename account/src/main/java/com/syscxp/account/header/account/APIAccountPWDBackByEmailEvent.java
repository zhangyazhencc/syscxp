package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;

public class APIAccountPWDBackByEmailEvent extends APIEvent {

    private AccountInventory inventory;

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }

    public APIAccountPWDBackByEmailEvent(String apiId) {
        super(apiId);
    }

    public APIAccountPWDBackByEmailEvent() {
        super(null);
    }

}
