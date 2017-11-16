package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

public class APIGetAliEdgeRouterReply extends APIQueryReply {
    private AliEdgeRouterInformationInventory inventory;
    private AliEdgeRouterInventory routerInventory;
    private boolean aliIdentityFailure;

    public boolean isAliIdentityFailure() {
        return aliIdentityFailure;
    }

    public void setAliIdentityFailure(boolean aliIdentityFailure) {
        this.aliIdentityFailure = aliIdentityFailure;
    }

    public AliEdgeRouterInventory getRouterInventory() {
        return routerInventory;
    }

    public void setRouterInventory(AliEdgeRouterInventory routerInventory) {
        this.routerInventory = routerInventory;
    }

    public AliEdgeRouterInformationInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInformationInventory inventory) {
        this.inventory = inventory;
    }
}
