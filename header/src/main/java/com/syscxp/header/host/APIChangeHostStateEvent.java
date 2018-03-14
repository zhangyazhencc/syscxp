package com.syscxp.header.host;

import com.syscxp.header.message.APIEvent;

public class APIChangeHostStateEvent extends APIEvent {
    /**
     * @desc see :ref:`HostInventory`
     */
    private HostInventory inventory;

    public APIChangeHostStateEvent() {
        super(null);
    }

    public APIChangeHostStateEvent(String apiId) {
        super(apiId);
    }

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }

}
