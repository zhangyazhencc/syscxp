package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIEvent;

public class APICreateReceiptPostAddressEvent extends APIEvent {
    private ReceiptPostAddressInventory inventory;

    public APICreateReceiptPostAddressEvent(String apiId) {super(apiId);}

    public APICreateReceiptPostAddressEvent(){}

    public ReceiptPostAddressInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptPostAddressInventory inventory) {
        this.inventory = inventory;
    }
}
