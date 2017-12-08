package com.syscxp.billing.header.renew;

import com.syscxp.header.message.APIEvent;

public class APIUpdateRenewPriceEvent extends APIEvent {

    private RenewInventory inventory;

    public APIUpdateRenewPriceEvent(String apiId) {super(apiId);}

    public APIUpdateRenewPriceEvent(){}

    public RenewInventory getInventory() {
        return inventory;
    }

    public void setInventory(RenewInventory inventory) {
        this.inventory = inventory;
    }
}
