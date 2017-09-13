package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-13
 */
@RestResponse(allTo = "inventory")
public class APIDeletePhysicalSwitchEvent extends APIEvent {
    private PhysicalSwitchInventory inventory;

    public APIDeletePhysicalSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIDeletePhysicalSwitchEvent() {}

    public PhysicalSwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(PhysicalSwitchInventory inventory) {
        this.inventory = inventory;
    }
}
