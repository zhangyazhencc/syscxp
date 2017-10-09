package com.syscxp.billing.header.receipt;

import com.syscxp.header.query.APIQueryReply;

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
