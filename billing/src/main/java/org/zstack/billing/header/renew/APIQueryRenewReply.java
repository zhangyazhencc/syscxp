package org.zstack.billing.header.renew;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryRenewReply extends APIQueryReply {

    private List<RenewInventory> inventories;

    public List<RenewInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<RenewInventory> inventories) {
        this.inventories = inventories;
    }
}
