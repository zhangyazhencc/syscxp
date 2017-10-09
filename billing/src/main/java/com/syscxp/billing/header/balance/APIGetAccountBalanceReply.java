package com.syscxp.billing.header.balance;


import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIGetAccountBalanceReply extends APIReply {

    private AccountBalanceInventory inventory;

    public AccountBalanceInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountBalanceInventory inventory) {
        this.inventory = inventory;
    }

    public static APIGetAccountBalanceReply __example__() {
        APIGetAccountBalanceReply reply = new APIGetAccountBalanceReply();
        AccountBalanceInventory inventory = new AccountBalanceInventory();
        reply.setInventory(inventory);
        return reply;
    }


}
