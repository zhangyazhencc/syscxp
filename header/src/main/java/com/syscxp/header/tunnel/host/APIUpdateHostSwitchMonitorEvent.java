package com.syscxp.header.tunnel.host;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
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
