package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-30
 */
@RestResponse(allTo = "inventory")
public class APICreateSwitchVlanEvent extends APIEvent {
    private SwitchVlanInventory inventory;

    public APICreateSwitchVlanEvent(){}

    public APICreateSwitchVlanEvent(String apiId){super(apiId);}

    public SwitchVlanInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchVlanInventory inventory) {
        this.inventory = inventory;
    }
}
