package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-13
 */
@RestResponse(allTo = "inventory")
public class APIDeleteSwitchEvent extends APIEvent {
    private SwitchInventory inventory;

    public APIDeleteSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSwitchEvent() {}

    public SwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchInventory inventory) {
        this.inventory = inventory;
    }
}
