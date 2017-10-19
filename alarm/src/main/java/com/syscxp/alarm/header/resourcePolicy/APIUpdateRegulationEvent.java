package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APIUpdateRegulationEvent extends APIEvent {

    private RegulationInventory inventory;

    public APIUpdateRegulationEvent(String apiId) {super(apiId);}

    public APIUpdateRegulationEvent(){}

    public RegulationInventory getInventory() {
        return inventory;
    }

    public void setInventory(RegulationInventory inventory) {
        this.inventory = inventory;
    }
}
