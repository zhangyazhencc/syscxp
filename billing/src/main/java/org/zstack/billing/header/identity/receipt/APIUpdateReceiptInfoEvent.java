package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIEvent;

public class APIUpdateReceiptInfoEvent extends APIEvent {
    private ReceiptInfoInventory inventory;

    public APIUpdateReceiptInfoEvent(String apiId) {super(apiId);}

    public APIUpdateReceiptInfoEvent(){}

    public ReceiptInfoInventory getInventory() {
        return inventory;
    }

    public void setInventory(ReceiptInfoInventory inventory) {
        this.inventory = inventory;
    }
}
