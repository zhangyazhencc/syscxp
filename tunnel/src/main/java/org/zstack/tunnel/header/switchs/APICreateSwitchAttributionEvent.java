package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-06
 */
@RestResponse(allTo = "inventory")
public class APICreateSwitchAttributionEvent extends APIEvent {
    private SwitchAttributionInventory inventory;

    public APICreateSwitchAttributionEvent(){}

    public APICreateSwitchAttributionEvent(String apiId){super(apiId);}

    public SwitchAttributionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchAttributionInventory inventory) {
        this.inventory = inventory;
    }
}
