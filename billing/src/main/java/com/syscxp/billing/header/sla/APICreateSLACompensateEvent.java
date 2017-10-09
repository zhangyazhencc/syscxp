package com.syscxp.billing.header.sla;

import com.syscxp.header.message.APIEvent;

public class APICreateSLACompensateEvent extends APIEvent {
    private SLACompensateInventory inventory;

    public APICreateSLACompensateEvent(String apiId) {super(apiId);}

    public APICreateSLACompensateEvent(){}

    public SLACompensateInventory getInventory() {
        return inventory;
    }

    public void setInventory(SLACompensateInventory inventory) {
        this.inventory = inventory;
    }
}