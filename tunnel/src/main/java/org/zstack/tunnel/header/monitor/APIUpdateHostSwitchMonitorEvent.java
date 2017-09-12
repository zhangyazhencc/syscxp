package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIUpdateHostSwitchMonitorEvent extends APIEvent {
    private HostSwitchMonitorInventory inventory;

    public APIUpdateHostSwitchMonitorEvent(){};

    public APIUpdateHostSwitchMonitorEvent(String apiId){super(apiId);}

    public HostSwitchMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostSwitchMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
