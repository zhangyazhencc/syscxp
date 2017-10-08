package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIEvent;

public class APIUpdateReceiptEvent extends APIEvent {
    private ReceiptInventory inventory;

    public APIUpdateReceiptEvent(String apiId) {super(apiId);}

    public APIUpdateReceiptEvent(){}

    public ReceiptInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptInventory inventory) {
        this.inventory = inventory;
    }

}
