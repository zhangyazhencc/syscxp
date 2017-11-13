package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/10/10
 */
public class APIUpdateTunnelExpireDateReply extends APIUpdateExpireDateReply {
    private TunnelInventory inventory;

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
