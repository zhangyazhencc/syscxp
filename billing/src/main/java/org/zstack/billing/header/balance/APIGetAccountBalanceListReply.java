package org.zstack.billing.header.balance;

import org.zstack.header.billing.AccountBalanceInventory;
import org.zstack.header.message.APIReply;
import org.zstack.header.rest.RestResponse;

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