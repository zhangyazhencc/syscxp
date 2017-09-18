package org.zstack.billing.header.balance;

import org.zstack.header.message.APIEvent;

public class APIDeleteAccountDischargeEvent extends APIEvent{
    private AccountDischargeInventory inventory;

    public APIDeleteAccountDischargeEvent(String apiId) {super(apiId);}

    public APIDeleteAccountDischargeEvent(){}

    public AccountDischargeInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountDischargeInventory inventory) {
        this.inventory = inventory;
    }
}
