package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

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