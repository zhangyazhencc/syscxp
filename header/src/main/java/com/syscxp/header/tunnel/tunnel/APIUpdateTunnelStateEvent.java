package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/10/31
 */
@RestResponse(allTo = "inventory")
public class APIUpdateTunnelStateEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIUpdateTunnelStateEvent(){}

    public APIUpdateTunnelStateEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
