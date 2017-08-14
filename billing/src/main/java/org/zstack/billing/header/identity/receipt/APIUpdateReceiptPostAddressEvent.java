package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIEvent;

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
