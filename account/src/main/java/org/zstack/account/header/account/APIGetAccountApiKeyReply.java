package org.zstack.account.header.account;


import org.zstack.header.message.APIReply;

public class APIGetAccountApiKeyReply extends APIReply {

    private AccountApiSecurityInventory inventory;

    public AccountApiSecurityInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountApiSecurityInventory inventory) {
        this.inventory = inventory;
    }
}
