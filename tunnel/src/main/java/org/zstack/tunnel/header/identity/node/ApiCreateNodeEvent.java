package org.zstack.tunnel.header.identity.node;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-21
 */
@RestResponse(allTo = "inventory")
public class ApiCreateNodeEvent extends APIEvent {
    private NodeInventory inventory;

    public ApiCreateNodeEvent(){}

    public ApiCreateNodeEvent(String apiId){super(apiId);}

    public NodeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NodeInventory inventory) {
        this.inventory = inventory;
    }
}
