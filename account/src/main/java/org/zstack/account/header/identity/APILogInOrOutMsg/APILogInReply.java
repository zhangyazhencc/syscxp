package org.zstack.account.header.identity.APILogInOrOutMsg;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIReply;

public class APILogInReply extends APIReply {
    private SessionInventory inventory;

    public SessionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SessionInventory inventory) {
        this.inventory = inventory;
    }
}
