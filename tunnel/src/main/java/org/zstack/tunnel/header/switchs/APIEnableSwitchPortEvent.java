package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-30
 */
@RestResponse(allTo = "inventory")
public class APIEnableSwitchPortEvent extends APIEvent {
    private SwitchPortInventory inventory;

    public APIEnableSwitchPortEvent(String apiId) {
        super(apiId);
    }

    public APIEnableSwitchPortEvent() {}

    public SwitchPortInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchPortInventory inventory) {
        this.inventory = inventory;
    }
}
