package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAliEdgeRouterEvent extends APIEvent{

    private Boolean AliIdentityFlag;

    private AliEdgeRouterInventory inventory;

    public APICreateAliEdgeRouterEvent(){}

    public Boolean getAliIdentityFlag() {
        return AliIdentityFlag;
    }

    public void setAliIdentityFlag(Boolean aliIdentityFlag) {
        AliIdentityFlag = aliIdentityFlag;
    }

    public APICreateAliEdgeRouterEvent(String apiId){super(apiId);}

    public AliEdgeRouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInventory inventory) {
        this.inventory = inventory;
    }
}
