package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAliEdgeRouterEvent extends APIEvent{

    private boolean aliIdentityFailure;

    private AliEdgeRouterInventory inventory;

    public APICreateAliEdgeRouterEvent(){}

    public Boolean getAliIdentityFailure() {
        return aliIdentityFailure;
    }

    public void setAliIdentityFailure(Boolean aliIdentityFailure) {
        this.aliIdentityFailure = aliIdentityFailure;
    }

    public APICreateAliEdgeRouterEvent(String apiId){super(apiId);}

    public AliEdgeRouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInventory inventory) {
        this.inventory = inventory;
    }
}
