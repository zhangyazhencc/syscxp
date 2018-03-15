package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountDiscountInventory;
import com.syscxp.header.message.APIEvent;

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