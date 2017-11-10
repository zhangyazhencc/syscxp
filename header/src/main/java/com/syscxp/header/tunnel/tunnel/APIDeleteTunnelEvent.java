package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-17
 */
@RestResponse(allTo = "inventory")
public class APIDeleteTunnelEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIDeleteTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteTunnelEvent() {}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
