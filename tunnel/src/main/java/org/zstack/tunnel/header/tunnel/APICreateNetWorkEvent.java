package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-07
 */
@RestResponse(allTo = "inventory")
public class APICreateNetWorkEvent extends APIEvent {

    private NetWorkInventory inventory;

    public APICreateNetWorkEvent(){}

    public APICreateNetWorkEvent(String apiId){super(apiId);}

    public NetWorkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetWorkInventory inventory) {
        this.inventory = inventory;
    }
}
