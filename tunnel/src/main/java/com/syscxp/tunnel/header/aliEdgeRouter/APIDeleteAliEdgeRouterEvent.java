package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteAliEdgeRouterEvent extends APIEvent{
    private AliEdgeRouterInventory inventory;

    private Boolean AliIdentityFlag;

    public APIDeleteAliEdgeRouterEvent(){}

    public Boolean getAliIdentityFlag() {
        return AliIdentityFlag;
    }

    public void setAliIdentityFlag(Boolean aliIdentityFlag) {
        AliIdentityFlag = aliIdentityFlag;
    }

    public APIDeleteAliEdgeRouterEvent(String apiId){super(apiId);}

    public AliEdgeRouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInventory inventory) {
        this.inventory = inventory;
    }
}
