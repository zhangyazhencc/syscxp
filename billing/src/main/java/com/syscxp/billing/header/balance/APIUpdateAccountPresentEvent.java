package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateAccountPresentEvent extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIUpdateAccountPresentEvent(String apiId) {super(apiId);}

    public APIUpdateAccountPresentEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }
}
