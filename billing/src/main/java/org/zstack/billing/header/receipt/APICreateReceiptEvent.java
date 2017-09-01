package org.zstack.billing.header.receipt;

import org.zstack.header.message.APIEvent;

public class APICreateReceiptEvent extends APIEvent {
    private ReceiptInventory inventory;

    public APICreateReceiptEvent(String apiId) {super(apiId);}

    public APICreateReceiptEvent(){}

    public ReceiptInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptInventory inventory) {
        this.inventory = inventory;
    }

}