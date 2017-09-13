package org.zstack.tunnel.header.endpoint;

import org.zstack.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-13.
 * @Description: .
 */
public class APIDeleteEndpointEvent extends APIEvent {
    EndpointInventory inventory;

    public APIDeleteEndpointEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteEndpointEvent() {

    }

    public EndpointInventory getInventory() {
        return inventory;
    }

    public void setInventory(EndpointInventory inventory) {
        this.inventory = inventory;
    }
}
