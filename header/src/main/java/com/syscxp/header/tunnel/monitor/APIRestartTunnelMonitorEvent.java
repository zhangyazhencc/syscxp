package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;
import com.syscxp.header.tunnel.tunnel.TunnelInventory;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIRestartTunnelMonitorEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIRestartTunnelMonitorEvent(){};

    public APIRestartTunnelMonitorEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
