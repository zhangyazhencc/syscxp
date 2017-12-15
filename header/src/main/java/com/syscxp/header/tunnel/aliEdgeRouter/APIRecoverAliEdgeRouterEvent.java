package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APIRecoverAliEdgeRouterEvent extends APIEvent {
    private AliEdgeRouterInventory routerInventory;

    private boolean recoverFlag;

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

    public boolean isRecoverFlag() {
        return recoverFlag;
    }

    public void setRecoverFlag(boolean recoverFlag) {
        this.recoverFlag = recoverFlag;
    }
}
