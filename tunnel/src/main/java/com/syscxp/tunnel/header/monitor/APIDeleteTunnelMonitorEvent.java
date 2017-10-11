package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIDeleteTunnelMonitorEvent extends APIEvent {
    private TunnelMonitorInventory inventory;

    public APIDeleteTunnelMonitorEvent(){};

    public APIDeleteTunnelMonitorEvent(String apiId){super(apiId);}

    public TunnelMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelMonitorInventory inventory) {
        this.inventory = inventory;
    }
}