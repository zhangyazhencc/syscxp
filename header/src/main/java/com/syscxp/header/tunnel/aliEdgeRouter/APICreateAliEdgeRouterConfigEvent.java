package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APICreateAliEdgeRouterConfigEvent extends APIEvent {
    private AliEdgeRouterConfigInventory inventory;

    public APICreateAliEdgeRouterConfigEvent(){}

    public APICreateAliEdgeRouterConfigEvent(String apiId){super(apiId);}

    public AliEdgeRouterConfigInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterConfigInventory inventory) {
        this.inventory = inventory;
    }
}
