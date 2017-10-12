package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteAliEdgeRouterConfigEvent extends APIEvent{
    private AliEdgeRouterConfigInventory inventory;

    public APIDeleteAliEdgeRouterConfigEvent(){}

    public APIDeleteAliEdgeRouterConfigEvent(String apiId){super(apiId);}

    public AliEdgeRouterConfigInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterConfigInventory inventory) {
        this.inventory = inventory;
    }
}
