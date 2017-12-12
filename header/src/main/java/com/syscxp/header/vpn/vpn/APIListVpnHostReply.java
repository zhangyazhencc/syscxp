package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.vpn.host.VpnHostInventory;

import java.util.List;
import java.util.Map;

public class APIListVpnHostReply extends APIReply {
    private Map<String, List<VpnHostInventory>> inventoryMap;

    public Map<String, List<VpnHostInventory>> getInventoryMap() {
        return inventoryMap;
    }

    public void setInventoryMap(Map<String, List<VpnHostInventory>> inventoryMap) {
        this.inventoryMap = inventoryMap;
    }
}

