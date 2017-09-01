package org.zstack.billing.header.balance;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIReChargeProxyEvent  extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIReChargeProxyEvent(String apiId) {super(apiId);}

    public APIReChargeProxyEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }
}
