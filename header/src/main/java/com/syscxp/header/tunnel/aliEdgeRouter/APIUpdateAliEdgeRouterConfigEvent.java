package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APIUpdateAliEdgeRouterConfigEvent extends APIEvent {
    private AliEdgeRouterConfigInventory inventory;

    public APIUpdateAliEdgeRouterConfigEvent(){}

    public APIUpdateAliEdgeRouterConfigEvent(String apiId){super(apiId);}

    public AliEdgeRouterConfigInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterConfigInventory inventory) {
        this.inventory = inventory;
    }
}
