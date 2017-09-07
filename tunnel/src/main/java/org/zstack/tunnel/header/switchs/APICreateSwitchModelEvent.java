package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-06
 */
@RestResponse(allTo = "inventory")
public class APICreateSwitchModelEvent extends APIEvent {
    private SwitchModelInventory inventory;

    public APICreateSwitchModelEvent(){}

    public APICreateSwitchModelEvent(String apiId){super(apiId);}

    public SwitchModelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchModelInventory inventory) {
        this.inventory = inventory;
    }
}
