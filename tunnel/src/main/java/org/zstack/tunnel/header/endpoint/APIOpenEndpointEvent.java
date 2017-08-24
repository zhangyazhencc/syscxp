package org.zstack.tunnel.header.endpoint;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-24
 */
@RestResponse(allTo = "inventory")
public class APIOpenEndpointEvent extends APIEvent {
    private EndpointInventory inventory;

    public APIOpenEndpointEvent(String apiId) {
        super(apiId);
    }

    public APIOpenEndpointEvent() {}

    public EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
