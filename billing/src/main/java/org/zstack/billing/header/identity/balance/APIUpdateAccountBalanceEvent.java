package org.zstack.billing.header.identity.balance;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAccountBalanceEvent  extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIUpdateAccountBalanceEvent(String apiId) {super(apiId);}

    public APIUpdateAccountBalanceEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }
}
