package org.zstack.billing.header.identity;

import org.zstack.header.message.APIEvent;

public class APIUpdateRenewEvent extends APIEvent{
    private RenewInventory inventory;

    public APIUpdateRenewEvent(String apiId) {super(apiId);}

    public APIUpdateRenewEvent(){}

    public RenewInventory getInventory() {
        return inventory;
    }

    public void setInventory(RenewInventory inventory) {
        this.inventory = inventory;
    }
}
