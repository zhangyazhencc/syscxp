package com.syscxp.account.header.account;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAccountContactsEvent extends APIEvent {
    private AccountContactsInventory inventory;

    public APICreateAccountContactsEvent(String apiId) {
        super(apiId);
    }

    public APICreateAccountContactsEvent() {
        super(null);
    }

    public AccountContactsInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountContactsInventory inventory) {
        this.inventory = inventory;
    }

}