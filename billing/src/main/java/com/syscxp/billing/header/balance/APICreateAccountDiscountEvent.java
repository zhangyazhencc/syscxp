package com.syscxp.billing.header.balance;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAccountDiscountEvent  extends APIEvent {

    private AccountDiscountInventory inventory;

    public APICreateAccountDiscountEvent(String apiId) {super(apiId);}

    public APICreateAccountDiscountEvent(){}

    public AccountDiscountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountDiscountInventory inventory) {
        this.inventory = inventory;
    }
}