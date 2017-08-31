package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-30
 */
@RestResponse(allTo = "inventory")
public class APIUpdateHostSwitchMonitorEvent extends APIEvent {

    private HostSwitchMonitorInventory inventory;

    public APIUpdateHostSwitchMonitorEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateHostSwitchMonitorEvent() {}

    public HostSwitchMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostSwitchMonitorInventory inventory) {
        this.inventory = inventory;
    }
}
