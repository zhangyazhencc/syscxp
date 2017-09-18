package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-14
 */
@RestResponse(allTo = "inventory")
public class APICreateNetWorkManualEvent extends APIEvent {
    private NetWorkInventory inventory;

    public APICreateNetWorkManualEvent(){}

    public APICreateNetWorkManualEvent(String apiId){super(apiId);}

    public NetWorkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetWorkInventory inventory) {
        this.inventory = inventory;
    }
}
