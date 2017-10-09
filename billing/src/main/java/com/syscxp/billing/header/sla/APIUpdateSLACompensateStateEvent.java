package com.syscxp.billing.header.sla;

import com.syscxp.header.message.APIEvent;

public class APIUpdateSLACompensateStateEvent   extends APIEvent {
    private SLACompensateInventory inventory;

    public APIUpdateSLACompensateStateEvent(String apiId) {super(apiId);}

    public APIUpdateSLACompensateStateEvent(){}

    public SLACompensateInventory getInventory() {
        return inventory;
    }

    public void setInventory(SLACompensateInventory inventory) {
        this.inventory = inventory;
    }
}