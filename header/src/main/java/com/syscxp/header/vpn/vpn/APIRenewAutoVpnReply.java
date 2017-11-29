package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;
import com.syscxp.header.tunnel.tunnel.InterfaceInventory;

@RestResponse(allTo = "inventory")
public class APIRenewAutoVpnReply extends APIReply {

    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
