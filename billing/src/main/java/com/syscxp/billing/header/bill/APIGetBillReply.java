package com.syscxp.billing.header.bill;

import com.syscxp.header.message.APIReply;

public class APIGetBillReply extends APIReply {

    private BillInventory inventory;

    public BillInventory getInventory() {
        return inventory;
    }

    public void setInventory(BillInventory inventory) {
        this.inventory = inventory;
    }

    public static APIGetBillReply __example__() {
        APIGetBillReply reply = new APIGetBillReply();
        BillInventory inventory = new BillInventory();
        reply.setInventory(inventory);
        return reply;
    }

}
