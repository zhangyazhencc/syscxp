package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-30
 */
@RestResponse(allTo = "inventory")
public class APICreateHostMonitorEvent extends APIEvent {

    private HostMonitorInventory inventory;

    public APICreateHostMonitorEvent(){}

    public APICreateHostMonitorEvent(String apiId){super(apiId);}

    public HostMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
