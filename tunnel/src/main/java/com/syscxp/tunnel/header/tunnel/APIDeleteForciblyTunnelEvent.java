package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/10/11
 */
@RestResponse(allTo = "inventory")
public class APIDeleteForciblyTunnelEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIDeleteForciblyTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteForciblyTunnelEvent() {}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
