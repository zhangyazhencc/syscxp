package com.syscxp.header.host;

import com.syscxp.header.message.APIEvent;

public class APIReconnectHostEvent extends APIEvent {
    /**
     * @desc see :ref:`HostInventory`
     */
    private HostInventory inventory;

    public APIReconnectHostEvent() {
        super(null);
    }

    public APIReconnectHostEvent(String apiId) {
        super(apiId);
    }

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }

}
