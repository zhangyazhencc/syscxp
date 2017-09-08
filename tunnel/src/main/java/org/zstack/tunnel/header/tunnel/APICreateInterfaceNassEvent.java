package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-08
 */
@RestResponse(allTo = "inventory")
public class APICreateInterfaceNassEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APICreateInterfaceNassEvent(){}

    public APICreateInterfaceNassEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
