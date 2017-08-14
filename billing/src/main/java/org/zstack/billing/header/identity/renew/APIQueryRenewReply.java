package org.zstack.billing.header.identity.renew;

import org.zstack.billing.header.identity.OrderInventory;
import org.zstack.billing.header.identity.RenewInventory;
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
