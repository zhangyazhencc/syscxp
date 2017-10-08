package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIEvent;

public class APIDeleteReceiptInfoEvent extends APIEvent {

    private ReceiptInfoInventory inventory;

    public APIDeleteReceiptInfoEvent(String apiId) {super(apiId);}

    public APIDeleteReceiptInfoEvent(){}

    public ReceiptInfoInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptInfoInventory inventory) {
        this.inventory = inventory;
    }
}
