package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2018/1/12
 */
@RestResponse(allTo = "inventory")
public class APIRenewAutoEdgeLineReply extends APIReply {

    private EdgeLineInventory inventory;

    public EdgeLineInventory getInventory() {
        return inventory;
    }

    public void setInventory(EdgeLineInventory inventory) {
        this.inventory = inventory;
    }
}
