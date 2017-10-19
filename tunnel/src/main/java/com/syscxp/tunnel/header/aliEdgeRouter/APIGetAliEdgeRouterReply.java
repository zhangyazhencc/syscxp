package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

public class APIGetAliEdgeRouterReply extends APIQueryReply {
    private AliEdgeRouterInformationInventory inventory;
    private AliEdgeRouterInventory routerInventory;
    private Boolean AliIdentityFlag;

    public Boolean getAliIdentityFlag() {
        return AliIdentityFlag;
    }

    public void setAliIdentityFlag(Boolean aliIdentityFlag) {
        AliIdentityFlag = aliIdentityFlag;
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
