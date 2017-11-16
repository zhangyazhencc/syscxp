package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteAliEdgeRouterEvent extends APIEvent{
    private AliEdgeRouterInventory inventory;

    private Boolean aliIdentityFailure;

    public APIDeleteAliEdgeRouterEvent(){}

    public Boolean getAliIdentityFailure() {
        return aliIdentityFailure;
    }

    public void setAliIdentityFailure(Boolean aliIdentityFailure) {
        this.aliIdentityFailure = aliIdentityFailure;
    }

    public APIDeleteAliEdgeRouterEvent(String apiId){super(apiId);}

    public AliEdgeRouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInventory inventory) {
        this.inventory = inventory;
    }
}
