package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIEvent;

public class APIDeleteReceiptPostAddressEvent extends APIEvent {
    private ReceiptPostAddressInventory inventory;

    public APIDeleteReceiptPostAddressEvent(String apiId) {super(apiId);}

    public APIDeleteReceiptPostAddressEvent(){}

    public ReceiptPostAddressInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptPostAddressInventory inventory) {
        this.inventory = inventory;
    }
}
