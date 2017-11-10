package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APISaveAliUserEvent extends APIEvent {
    private AliUserInventory inventory;

    public APISaveAliUserEvent(){}

    public APISaveAliUserEvent(String apiId){super(apiId);}

    public AliUserInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliUserInventory inventory) {
        this.inventory = inventory;
    }
}
