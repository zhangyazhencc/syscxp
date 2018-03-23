package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateAccountCreditEvent extends APIEvent {

    private AccountBalanceInventory inventory;

    public APIUpdateAccountCreditEvent(String apiId) {super(apiId);}

    public APIUpdateAccountCreditEvent(){}

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }

}
