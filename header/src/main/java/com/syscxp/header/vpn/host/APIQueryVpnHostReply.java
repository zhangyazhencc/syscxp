package com.syscxp.header.vpn.host;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryVpnHostReply extends APIQueryReply {
    private List<VpnHostInventory> inventories;

    public List<VpnHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<VpnHostInventory> inventories) {
        this.inventories = inventories;
    }

}
