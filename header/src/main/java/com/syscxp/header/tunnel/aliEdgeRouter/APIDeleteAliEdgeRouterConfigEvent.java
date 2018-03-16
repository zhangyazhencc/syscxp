package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

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
