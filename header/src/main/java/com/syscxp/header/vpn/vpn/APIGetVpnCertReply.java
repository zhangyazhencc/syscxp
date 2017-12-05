package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.vpn.agent.ClientConfInventory;

public class APIGetVpnCertReply extends APIReply {
    public ClientConfInventory getInventory() {
        return inventory;
    }

    public void setInventory(ClientConfInventory inventory) {
        this.inventory = inventory;
    }

    private ClientConfInventory inventory;

}
