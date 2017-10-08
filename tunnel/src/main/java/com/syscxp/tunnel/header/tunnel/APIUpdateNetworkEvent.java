package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

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
