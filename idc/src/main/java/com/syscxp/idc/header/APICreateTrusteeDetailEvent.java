package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APICreateTrusteeDetailEvent extends APIEvent {

    private TrusteeDetailInventory inventory;

    public APICreateTrusteeDetailEvent(){}

    public APICreateTrusteeDetailEvent(String apiId){super(apiId);}

    public TrusteeDetailInventory getInventory() {
        return inventory;
    }

    public void setInventory(TrusteeDetailInventory inventory) {
        this.inventory = inventory;
    }

}
