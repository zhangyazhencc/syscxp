package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APIDeleteRegulationEvent  extends APIEvent {

    private RegulationInventory inventory;

    public APIDeleteRegulationEvent(String apiId) {super(apiId);}

    public APIDeleteRegulationEvent(){}

    public RegulationInventory getInventory() {
        return inventory;
    }

    public void setInventory(RegulationInventory inventory) {
        this.inventory = inventory;
    }
}
