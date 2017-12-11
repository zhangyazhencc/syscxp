package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.vpn.host.VpnHostInventory;

import java.util.List;

public class APIListVpnHostReply extends APIReply {
    List<VpnHostInventory> inventories;

    public List<VpnHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnHostInventory> inventories) {
        this.inventories = inventories;
    }
}
