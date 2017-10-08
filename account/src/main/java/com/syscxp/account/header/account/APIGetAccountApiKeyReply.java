package com.syscxp.account.header.account;


import com.syscxp.header.message.APIReply;

public class APIGetAccountApiKeyReply extends APIReply {

    private AccountApiSecurityInventory inventory;

    public AccountApiSecurityInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountApiSecurityInventory inventory) {
        this.inventory = inventory;
    }
}
