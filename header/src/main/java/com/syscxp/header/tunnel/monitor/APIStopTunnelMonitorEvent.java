package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIStopTunnelMonitorEvent extends APIEvent {
    private TunnelMonitorInventory inventory;

    public APIStopTunnelMonitorEvent(){};

    public APIStopTunnelMonitorEvent(String apiId){super(apiId);}

    public TunnelMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
