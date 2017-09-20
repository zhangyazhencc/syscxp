package org.zstack.billing.header.balance;

import org.zstack.header.message.APIReply;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIRechargeReply  extends APIReply {

    private String inventory;

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
}
