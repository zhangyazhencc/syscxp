package com.syscxp.billing.header.balance;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAccountDiscountEvent extends APIEvent {

    private AccountDiscountInventory inventory;

    public APIUpdateAccountDiscountEvent(String apiId) {super(apiId);}

    public APIUpdateAccountDiscountEvent(){}

    public AccountDiscountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountDiscountInventory inventory) {
        this.inventory = inventory;
    }
}