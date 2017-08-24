package org.zstack.tunnel.header.endpoint;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-23
 */
@RestResponse(allTo = "inventory")
public class APICreateEndpointEvent extends APIEvent {
    private EndpointInventory inventory;

    public APICreateEndpointEvent(){}

    public APICreateEndpointEvent(String apiId){super(apiId);}

    public EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
