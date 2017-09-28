package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Create by DCY on 2017/9/28
 */
@RestResponse(allTo = "inventory")
public class APIUpdateInterfaceExpireDateEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APIUpdateInterfaceExpireDateEvent(){}

    public APIUpdateInterfaceExpireDateEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
