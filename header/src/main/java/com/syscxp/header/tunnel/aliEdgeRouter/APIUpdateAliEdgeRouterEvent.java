package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAliEdgeRouterEvent extends APIEvent{
    private AliEdgeRouterInformationInventory inventory;
    private AliEdgeRouterInventory routerInventory;


    private boolean aliIdentityFailure;

    public APIUpdateAliEdgeRouterEvent() {}

    public boolean isAliIdentityFailure() {
        return aliIdentityFailure;
    }

    public void setAliIdentityFailure(boolean aliIdentityFailure) {
        this.aliIdentityFailure = aliIdentityFailure;
    }

    public APIUpdateAliEdgeRouterEvent(String apiId) {
        super(apiId);
    }

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
