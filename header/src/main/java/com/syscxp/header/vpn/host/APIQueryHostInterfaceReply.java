package com.syscxp.header.vpn.host;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryHostInterfaceReply extends APIQueryReply {
    List<HostInterfaceInventory> inventories;

    public List<HostInterfaceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostInterfaceInventory> inventories) {
        this.inventories = inventories;
    }
}
