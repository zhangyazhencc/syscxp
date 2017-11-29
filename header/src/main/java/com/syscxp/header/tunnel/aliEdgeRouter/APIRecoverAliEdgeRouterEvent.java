package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APIRecoverAliEdgeRouterEvent extends APIEvent {
    private AliEdgeRouterInventory routerInventory;

    public APIRecoverAliEdgeRouterEvent() {
    }

    public APIRecoverAliEdgeRouterEvent(String apiId) {
        super(apiId);
    }


    public AliEdgeRouterInventory getRouterInventory() {
        return routerInventory;
    }

    public void setRouterInventory(AliEdgeRouterInventory routerInventory) {
        this.routerInventory = routerInventory;
    }

}
