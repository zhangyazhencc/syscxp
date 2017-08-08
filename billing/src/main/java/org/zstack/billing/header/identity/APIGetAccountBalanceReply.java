package org.zstack.billing.header.identity;


import org.zstack.header.message.APIReply;
import org.zstack.header.rest.RestResponse;

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
