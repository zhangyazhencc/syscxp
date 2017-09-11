package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-11
 */
@RestResponse(allTo = "inventory")
public class APICreateInterfaceBossEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APICreateInterfaceBossEvent(){}

    public APICreateInterfaceBossEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
