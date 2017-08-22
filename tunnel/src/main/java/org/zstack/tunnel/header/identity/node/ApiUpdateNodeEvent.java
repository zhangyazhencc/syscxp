package org.zstack.tunnel.header.identity.node;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-21
 */
@RestResponse(allTo = "inventory")
public class ApiUpdateNodeEvent extends APIEvent {

    private NodeInventory inventory;

    public ApiUpdateNodeEvent(String apiId) {
        super(apiId);
    }

    public ApiUpdateNodeEvent() {}

    public NodeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NodeInventory inventory) {
        this.inventory = inventory;
    }
}
