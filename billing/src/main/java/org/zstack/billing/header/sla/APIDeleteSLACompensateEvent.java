package org.zstack.billing.header.sla;

import org.zstack.header.message.APIEvent;

public class APIDeleteSLACompensateEvent  extends APIEvent {
    private SLACompensateInventory inventory;

    public APIDeleteSLACompensateEvent(String apiId) {super(apiId);}

    public APIDeleteSLACompensateEvent(){}

    public SLACompensateInventory getInventory() {
        return inventory;
    }

    public void setInventory(SLACompensateInventory inventory) {
        this.inventory = inventory;
    }
}