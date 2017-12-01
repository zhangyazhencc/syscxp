package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;

public class APIAccountPWDBackByPhoneEvent extends APIEvent {

    private AccountInventory inventory;

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }

    public APIAccountPWDBackByPhoneEvent(String apiId) {
        super(apiId);
    }

    public APIAccountPWDBackByPhoneEvent() {
        super(null);
    }

}
