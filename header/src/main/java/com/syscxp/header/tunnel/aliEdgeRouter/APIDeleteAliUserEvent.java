package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APIDeleteAliUserEvent extends APIEvent {
    private AliUserInventory inventory;

    public APIDeleteAliUserEvent(){}

    public APIDeleteAliUserEvent(String apiId){super(apiId);}

    public AliUserInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliUserInventory inventory) {
        this.inventory = inventory;
    }
}
