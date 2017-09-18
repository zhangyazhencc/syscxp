package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-14
 */
@RestResponse(allTo = "inventory")
public class APIDeleteInterfaceEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APIDeleteInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteInterfaceEvent() {}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
