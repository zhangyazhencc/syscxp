package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIEvent;

public class APIUpdateReceiptPostAddressEvent extends APIEvent {
    private ReceiptPostAddressInventory inventory;

    public APIUpdateReceiptPostAddressEvent(String apiId) {super(apiId);}

    public APIUpdateReceiptPostAddressEvent(){}

    public ReceiptPostAddressInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptPostAddressInventory inventory) {
        this.inventory = inventory;
    }
}
