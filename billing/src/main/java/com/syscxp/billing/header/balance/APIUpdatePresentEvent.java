package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdatePresentEvent extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIUpdatePresentEvent(String apiId) {super(apiId);}

    public APIUpdatePresentEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }
}
