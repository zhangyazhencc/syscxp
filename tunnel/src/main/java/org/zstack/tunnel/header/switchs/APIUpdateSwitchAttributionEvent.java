package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-06
 */
@RestResponse(allTo = "inventory")
public class APIUpdateSwitchAttributionEvent extends APIEvent {
    private SwitchAttributionInventory inventory;

    public APIUpdateSwitchAttributionEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSwitchAttributionEvent() {}

    public SwitchAttributionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchAttributionInventory inventory) {
        this.inventory = inventory;
    }
}
