package org.zstack.tunnel.header.node;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-21
 */
@RestResponse(allTo = "inventory")
public class APIUpdateNodeEvent extends APIEvent {

    private NodeInventory inventory;

    public APIUpdateNodeEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateNodeEvent() {}

    public NodeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NodeInventory inventory) {
        this.inventory = inventory;
    }
}
