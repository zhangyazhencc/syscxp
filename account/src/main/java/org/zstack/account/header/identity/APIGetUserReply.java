package org.zstack.account.header.identity;


import org.zstack.header.message.APIReply;

public class APIGetUserReply extends APIReply {

    private UserInventory inventory;

    public UserInventory getInventory() {
        return inventory;
    }

    public void setInventory(UserInventory inventory) {
        this.inventory = inventory;
    }
}
