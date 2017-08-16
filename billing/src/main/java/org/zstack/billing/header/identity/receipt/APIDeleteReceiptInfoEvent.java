package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIEvent;

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
