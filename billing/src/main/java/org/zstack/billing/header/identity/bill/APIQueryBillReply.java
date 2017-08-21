package org.zstack.billing.header.identity.bill;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryBillReply extends APIQueryReply {

    private List<BillInventory> inventories;

    public List<BillInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<BillInventory> inventories) {
        this.inventories = inventories;
    }
}
