package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-08-30
 */
@RestResponse(allTo = "inventory")
public class APICreateHostEvent extends APIEvent {
    private HostInventory inventory;

    public APICreateHostEvent(){}

    public APICreateHostEvent(String apiId){super(apiId);}

    public HostInventory getInventory() {
        return inventory;
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }
}
