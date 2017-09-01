package org.zstack.billing.header.receipt;

import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQueryReceiptPostAddressReply extends APIQueryReply {

    private List<ReceiptPostAddressInventory> inventories;

    public List<ReceiptPostAddressInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ReceiptPostAddressInventory> inventories) {
        this.inventories = inventories;
    }
}
