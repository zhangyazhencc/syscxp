package org.zstack.billing.header.receipt;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryReceiptInfoReply extends APIQueryReply {

    private List<ReceiptInfoInventory> inventories;

    public List<ReceiptInfoInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ReceiptInfoInventory> inventories) {
        this.inventories = inventories;
    }
}
