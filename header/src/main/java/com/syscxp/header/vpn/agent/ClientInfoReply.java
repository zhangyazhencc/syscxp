package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.vpn.VpnCertInventory;
import com.syscxp.header.vpn.vpn.VpnInventory;
import com.syscxp.header.vpn.vpn.VpnVO;

public class ClientInfoReply extends MessageReply {
    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
