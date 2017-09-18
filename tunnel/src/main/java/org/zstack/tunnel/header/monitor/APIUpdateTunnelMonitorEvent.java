package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIUpdateTunnelMonitorEvent extends APIEvent {
    private TunnelMonitorInventory inventory;

    public APIUpdateTunnelMonitorEvent(){};

    public APIUpdateTunnelMonitorEvent(String apiId){super(apiId);}

    public TunnelMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
