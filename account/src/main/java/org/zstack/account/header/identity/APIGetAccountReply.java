package org.zstack.account.header.identity;


import org.zstack.header.message.APIReply;

public class APIGetAccountReply extends APIReply {

    private AccountInventory inventory;

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }
}
