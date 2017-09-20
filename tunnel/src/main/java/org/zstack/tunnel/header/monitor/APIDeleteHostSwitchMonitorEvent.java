package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIDeleteHostSwitchMonitorEvent extends APIEvent {
    private HostSwitchMonitorInventory inventory;

    public APIDeleteHostSwitchMonitorEvent(){};

    public APIDeleteHostSwitchMonitorEvent(String apiId){super(apiId);}

    public HostSwitchMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostSwitchMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
