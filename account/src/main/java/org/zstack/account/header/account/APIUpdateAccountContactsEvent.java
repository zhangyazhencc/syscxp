package org.zstack.account.header.account;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")


public class APIUpdateAccountContactsEvent extends APIEvent {
    private AccountContactsInventory inventory;

    public APIUpdateAccountContactsEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateAccountContactsEvent() {
        super(null);
    }

    public AccountContactsInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountContactsInventory inventory) {
        this.inventory = inventory;
    }

}
