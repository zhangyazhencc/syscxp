package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-11
 */
@RestResponse(allTo = "inventory")
public class APICreateTunnelEvent extends APIEvent {

    private TunnelInventory inventory;

    public APICreateTunnelEvent(){}

    public APICreateTunnelEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
