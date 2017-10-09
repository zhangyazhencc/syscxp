package com.syscxp.billing.header.balance;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAccountDischargeEvent extends APIEvent {

    private AccountDischargeInventory inventory;

    public APIUpdateAccountDischargeEvent(String apiId) {super(apiId);}

    public APIUpdateAccountDischargeEvent(){}

    public AccountDischargeInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountDischargeInventory inventory) {
        this.inventory = inventory;
    }
}