package com.syscxp.idc.header.trustee;

import com.syscxp.header.message.APIEvent;

public class APIUpdateTrusteeEvent extends APIEvent{

    private TrusteeInventory inventory;

    public APIUpdateTrusteeEvent(){}

    public APIUpdateTrusteeEvent(String apiId){super(apiId);}

    public TrusteeInventory getInventory() {
        return inventory;
    }

    public void setInventory(TrusteeInventory inventory) {
        this.inventory = inventory;
    }
}
