package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-07
 */
@RestResponse(allTo = "inventory")
public class APIUpdateNetWorkEvent extends APIEvent {

    private NetWorkInventory inventory;

    public APIUpdateNetWorkEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateNetWorkEvent() {}

    public NetWorkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetWorkInventory inventory) {
        this.inventory = inventory;
    }
}
