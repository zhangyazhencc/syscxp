package org.zstack.billing.header.identity.sla;

import org.zstack.header.message.APIEvent;

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