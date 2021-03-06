package com.syscxp.header.host;

import com.syscxp.header.message.APIEvent;


public class APIAddHostEvent extends APIEvent {
    /**
     * @desc see :ref:`HostInventory`
     */
    private HostInventory inventory;

    public APIAddHostEvent() {
        super(null);
    }

    public APIAddHostEvent(String apiId) {
        super(apiId);
    }

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }

}
