package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIEvent;

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
