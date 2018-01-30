package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2018/1/26
 */
@RestResponse(allTo = "inventory")
public class APIUnsupportTunnelEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIUnsupportTunnelEvent(){}

    public APIUnsupportTunnelEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }

}
