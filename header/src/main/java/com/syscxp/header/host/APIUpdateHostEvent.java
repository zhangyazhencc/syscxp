package com.syscxp.header.host;

import com.syscxp.header.message.APIEvent;

public class APIUpdateHostEvent extends APIEvent {
    private HostInventory inventory;

    public APIUpdateHostEvent() {
    }

    public APIUpdateHostEvent(String apiId) {
        super(apiId);
    }

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }

}
