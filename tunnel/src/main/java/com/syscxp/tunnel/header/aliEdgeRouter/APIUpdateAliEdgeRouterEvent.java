package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAliEdgeRouterEvent extends APIEvent{
    private AliEdgeRouterInventory inventory;

    public APIUpdateAliEdgeRouterEvent() {}
    public APIUpdateAliEdgeRouterEvent(String apiId) {
        super(apiId);
    }

    public AliEdgeRouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInventory inventory) {
        this.inventory = inventory;
    }
}
