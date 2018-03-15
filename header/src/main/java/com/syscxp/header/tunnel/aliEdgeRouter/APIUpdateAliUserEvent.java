package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APIUpdateAliUserEvent extends APIEvent{
    private AliUserInventory inventory;

    public APIUpdateAliUserEvent(){}

    public APIUpdateAliUserEvent(String apiId) {
        super(apiId);
    }

    public AliUserInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliUserInventory inventory) {
        this.inventory = inventory;
    }
}
