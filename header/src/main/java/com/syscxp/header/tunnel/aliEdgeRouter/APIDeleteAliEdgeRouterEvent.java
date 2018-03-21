package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;

public class APIDeleteAliEdgeRouterEvent extends APIEvent{

    private AliEdgeRouterInformationInventory inventory;
    private AliEdgeRouterInventory routerInventory;

    private Boolean aliIdentityFailure;

    public APIDeleteAliEdgeRouterEvent(){}

    public Boolean getAliIdentityFailure() {
        return aliIdentityFailure;
    }

    public void setAliIdentityFailure(Boolean aliIdentityFailure) {
        this.aliIdentityFailure = aliIdentityFailure;
    }

    public APIDeleteAliEdgeRouterEvent(String apiId){super(apiId);}

    public AliEdgeRouterInformationInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInformationInventory inventory) {
        this.inventory = inventory;
    }

    public AliEdgeRouterInventory getRouterInventory() {
        return routerInventory;
    }

    public void setRouterInventory(AliEdgeRouterInventory routerInventory) {
        this.routerInventory = routerInventory;
    }
}
