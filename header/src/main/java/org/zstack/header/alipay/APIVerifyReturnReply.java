package org.zstack.header.alipay;

import org.zstack.header.message.APIReply;

public class APIVerifyReturnReply  extends APIReply {

    private boolean inventory;

    public boolean getInventory() {
        return inventory;
    }

    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }
}