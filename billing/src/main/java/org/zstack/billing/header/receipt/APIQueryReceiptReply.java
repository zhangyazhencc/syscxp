package org.zstack.billing.header.receipt;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryReceiptReply extends APIQueryReply {

    private List<ReceiptInventory> inventories;

    public List<ReceiptInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ReceiptInventory> inventories) {
        this.inventories = inventories;
    }
}
