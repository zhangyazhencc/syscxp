package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-07
 */
@RestResponse(allTo = "inventory")
public class APICreateNetworkEvent extends APIEvent {

    private NetworkInventory inventory;

    public APICreateNetworkEvent(){}

    public APICreateNetworkEvent(String apiId){super(apiId);}

    public NetworkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetworkInventory inventory) {
        this.inventory = inventory;
    }
}
