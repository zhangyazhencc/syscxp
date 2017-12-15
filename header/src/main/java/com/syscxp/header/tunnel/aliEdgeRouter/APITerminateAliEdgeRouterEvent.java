package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APITerminateAliEdgeRouterEvent extends APIEvent{
    private AliEdgeRouterInventory routerInventory;
    private boolean terminateFlag;

    public APITerminateAliEdgeRouterEvent() {
    }

    public APITerminateAliEdgeRouterEvent(String apiId) {
        super(apiId);
    }

    public AliEdgeRouterInventory getRouterInventory() {
        return routerInventory;
    }

    public void setRouterInventory(AliEdgeRouterInventory routerInventory) {
        this.routerInventory = routerInventory;
    }

    public boolean isTerminateFlag() {
        return terminateFlag;
    }

    public void setTerminateFlag(boolean terminateFlag) {
        this.terminateFlag = terminateFlag;
    }
}
