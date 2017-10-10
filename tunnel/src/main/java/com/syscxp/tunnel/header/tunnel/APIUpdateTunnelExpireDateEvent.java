package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/10/10
 */
@RestResponse(allTo = "inventory")
public class APIUpdateTunnelExpireDateEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIUpdateTunnelExpireDateEvent(){}

    public APIUpdateTunnelExpireDateEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
