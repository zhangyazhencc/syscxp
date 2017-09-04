package org.zstack.billing.header.receipt;

import org.zstack.header.message.APIEvent;

public class APIConfirmReceiptEvent extends APIEvent {
    private ReceiptInventory inventory;

    public APIConfirmReceiptEvent(String apiId) {super(apiId);}

    public APIConfirmReceiptEvent(){}

    public ReceiptInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptInventory inventory) {
        this.inventory = inventory;
    }

}
