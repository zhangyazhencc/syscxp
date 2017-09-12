package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.rest.RestResponse;
import org.zstack.tunnel.header.host.HostMonitorInventory;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APICreateHostSwitchMonitorEvent extends APIEvent {
    private HostSwitchMonitorInventory inventory;

    public APICreateHostSwitchMonitorEvent(){};

    public APICreateHostSwitchMonitorEvent(String apiId){super(apiId);}

    public HostSwitchMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostSwitchMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
