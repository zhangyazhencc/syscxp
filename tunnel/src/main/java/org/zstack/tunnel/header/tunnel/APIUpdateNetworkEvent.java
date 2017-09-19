package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-07
 */
@RestResponse(allTo = "inventory")
public class APIUpdateNetworkEvent extends APIEvent {

    private NetworkInventory inventory;

    public APIUpdateNetworkEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateNetworkEvent() {}

    public NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetworkInventory inventory) {
        this.inventory = inventory;
    }
}
