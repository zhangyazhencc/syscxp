package org.zstack.billing.header.identity;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryOrderReply  extends APIQueryReply {

    private List<OrderInventory> inventories;

    public List<OrderInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OrderInventory> inventories) {
        this.inventories = inventories;
    }
}
