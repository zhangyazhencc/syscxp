package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountDiscountInventory;
import com.syscxp.header.message.APIEvent;

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