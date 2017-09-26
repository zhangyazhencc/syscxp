package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Create by DCY on 2017/9/26
 */
@RestResponse(allTo = "inventory")
public class APICreateMonitorCidrEvent extends APIEvent {
    private MonitorCidrInventory inventory;

    public APICreateMonitorCidrEvent(){}

    public APICreateMonitorCidrEvent(String apiId){super(apiId);}

    public MonitorCidrInventory getInventory() {
        return inventory;
    }

    public void setInventory(MonitorCidrInventory inventory) {
        this.inventory = inventory;
    }
}
