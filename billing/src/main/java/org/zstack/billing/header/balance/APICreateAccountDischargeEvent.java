package org.zstack.billing.header.balance;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAccountDischargeEvent  extends APIEvent {

    private AccountDischargeInventory inventory;

    public APICreateAccountDischargeEvent(String apiId) {super(apiId);}

    public APICreateAccountDischargeEvent(){}

    public AccountDischargeInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountDischargeInventory inventory) {
        this.inventory = inventory;
    }
}