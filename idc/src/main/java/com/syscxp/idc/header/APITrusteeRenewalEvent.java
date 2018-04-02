package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APITrusteeRenewalEvent extends APIEvent{

    private TrusteeInventory inventory;

    public APITrusteeRenewalEvent(){}

    public APITrusteeRenewalEvent(String apiId){super(apiId);}

    public TrusteeInventory getInventory() {
        return inventory;
    }

    public void setInventory(TrusteeInventory inventory) {
        this.inventory = inventory;
    }
}
