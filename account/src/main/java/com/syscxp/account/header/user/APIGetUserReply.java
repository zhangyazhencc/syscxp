package com.syscxp.account.header.user;


import com.syscxp.header.message.APIReply;

public class APIGetUserReply extends APIReply {

    private UserInventory inventory;

    public UserInventory getInventory() {
        return inventory;
    }

    public void setInventory(UserInventory inventory) {
        this.inventory = inventory;
    }
}
