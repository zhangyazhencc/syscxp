package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountType;
import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
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
