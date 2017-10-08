package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

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
