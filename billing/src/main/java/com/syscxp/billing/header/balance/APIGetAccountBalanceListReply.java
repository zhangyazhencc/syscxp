package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountBalanceInventory;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIGetAccountBalanceListReply extends APIReply {

    private List<AccountBalanceInventory> inventories;

    public List<AccountBalanceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountBalanceInventory> inventories) {
        this.inventories = inventories;
    }
}