package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
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