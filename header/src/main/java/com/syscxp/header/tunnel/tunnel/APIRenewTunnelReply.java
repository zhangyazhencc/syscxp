package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/15
 */
@RestResponse(fieldsTo = {"inventory"})
public class APIRenewTunnelReply extends APIReply {
    private TunnelInventory inventory;

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
