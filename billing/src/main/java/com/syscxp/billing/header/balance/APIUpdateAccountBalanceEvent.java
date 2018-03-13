package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateAccountBalanceEvent extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIUpdateAccountBalanceEvent(String apiId) {super(apiId);}

    public APIUpdateAccountBalanceEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }

}
