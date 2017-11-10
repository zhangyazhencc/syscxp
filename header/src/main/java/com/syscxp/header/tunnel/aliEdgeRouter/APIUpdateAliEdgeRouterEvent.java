package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAliEdgeRouterEvent extends APIEvent{
    private AliEdgeRouterInventory inventory;

    private String AliIdentityFlag;

    public APIUpdateAliEdgeRouterEvent() {}

    public String getAliIdentityFlag() {
        return AliIdentityFlag;
    }

    public void setAliIdentityFlag(String aliIdentityFlag) {
        AliIdentityFlag = aliIdentityFlag;
    }

    public APIUpdateAliEdgeRouterEvent(String apiId) {
        super(apiId);
    }

    public AliEdgeRouterInventory getInventory() {
        return inventory;
    }

    public void setInventory(AliEdgeRouterInventory inventory) {
        this.inventory = inventory;
    }
}
