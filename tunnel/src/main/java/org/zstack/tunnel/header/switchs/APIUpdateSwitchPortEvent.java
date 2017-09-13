package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-13
 */
@RestResponse(allTo = "inventory")
public class APIUpdateSwitchPortEvent extends APIEvent {
    private SwitchPortInventory inventory;

    public APIUpdateSwitchPortEvent(){}

    public APIUpdateSwitchPortEvent(String apiId){super(apiId);}

    public SwitchPortInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchPortInventory inventory) {
        this.inventory = inventory;
    }
}
