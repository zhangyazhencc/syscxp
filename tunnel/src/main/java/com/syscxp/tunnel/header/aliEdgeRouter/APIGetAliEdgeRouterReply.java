package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

public class APIGetAliEdgeRouterReply extends APIQueryReply {
    private AliEdgeRouterInformationInventory inventory;
    private AliEdgeRouterInventory routerInventory;
    private String AliIdentityFlag;

    public String getAliIdentityFlag() {
        return AliIdentityFlag;
    }

    public void setAliIdentityFlag(String aliIdentityFlag) {
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
