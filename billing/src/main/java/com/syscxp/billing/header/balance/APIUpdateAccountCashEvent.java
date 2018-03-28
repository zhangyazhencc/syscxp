package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateAccountCashEvent extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIUpdateAccountCashEvent(String apiId) {super(apiId);}

    public APIUpdateAccountCashEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }

}
