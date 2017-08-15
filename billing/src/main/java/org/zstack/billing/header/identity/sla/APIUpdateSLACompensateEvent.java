package org.zstack.billing.header.identity.sla;

import org.zstack.header.message.APIEvent;

public class APIUpdateSLACompensateEvent extends APIEvent {
        private SLACompensateInventory inventory;

    public APIUpdateSLACompensateEvent(String apiId) {super(apiId);}

    public APIUpdateSLACompensateEvent(){}

    public SLACompensateInventory getInventory() {
        return inventory;
    }

    public void setInventory(SLACompensateInventory inventory) {
        this.inventory = inventory;
    }
}