package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-08
 */
@RestResponse(allTo = "inventory")
public class APICreateInterfaceEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APICreateInterfaceEvent(){}

    public APICreateInterfaceEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
