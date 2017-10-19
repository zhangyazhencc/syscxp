package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APICreateRegulationEvent extends APIEvent {

    private RegulationInventory inventory;

    public APICreateRegulationEvent(String apiId) {super(apiId);}

    public APICreateRegulationEvent(){}

    public RegulationInventory getInventory() {
        return inventory;
    }

    public void setInventory(RegulationInventory inventory) {
        this.inventory = inventory;
    }
}
