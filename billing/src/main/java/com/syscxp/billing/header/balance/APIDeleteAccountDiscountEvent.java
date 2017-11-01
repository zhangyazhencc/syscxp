package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountDiscountInventory;
import com.syscxp.header.message.APIEvent;

public class APIDeleteAccountDiscountEvent extends APIEvent{
    private AccountDiscountInventory inventory;

    public APIDeleteAccountDiscountEvent(String apiId) {super(apiId);}

    public APIDeleteAccountDiscountEvent(){}

    public AccountDiscountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountDiscountInventory inventory) {
        this.inventory = inventory;
    }
}
