package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2018/1/12
 */
@RestResponse(allTo = "inventory")
public class APIDeleteEdgeLineEvent extends APIEvent {
    private EdgeLineInventory inventory;

    public APIDeleteEdgeLineEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteEdgeLineEvent() {}

    public EdgeLineInventory getInventory() {
        return inventory;
    }

    public void setInventory(EdgeLineInventory inventory) {
        this.inventory = inventory;
    }
}
