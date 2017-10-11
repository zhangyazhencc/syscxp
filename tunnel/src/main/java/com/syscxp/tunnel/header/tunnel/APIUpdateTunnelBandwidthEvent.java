package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/10/10
 */
@RestResponse(allTo = "inventory")
public class APIUpdateTunnelBandwidthEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIUpdateTunnelBandwidthEvent(){}

    public APIUpdateTunnelBandwidthEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
