package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIEvent;

public class APICreateReceiptInfoEvent extends APIEvent {
    private ReceiptInfoInventory inventory;

    public APICreateReceiptInfoEvent(String apiId) {super(apiId);}

    public APICreateReceiptInfoEvent(){}

    public ReceiptInfoInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptInfoInventory inventory) {
        this.inventory = inventory;
    }

}
