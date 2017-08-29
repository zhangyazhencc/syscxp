package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-29
 */
@RestResponse(allTo = "inventory")
public class APIPrivateSwitchEvent extends APIEvent {
    private SwitchInventory inventory;

    public APIPrivateSwitchEvent(String apiId) {
        super(apiId);
    }

    public APIPrivateSwitchEvent() {}

    public SwitchInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchInventory inventory) {
        this.inventory = inventory;
    }
}
