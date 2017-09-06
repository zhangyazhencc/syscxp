package org.zstack.billing.header.receipt;

import org.zstack.header.message.APIEvent;

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
