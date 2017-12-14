package com.syscxp.header.tunnel.host;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryNettoolMonitorHostReply extends APIQueryReply {
    private List<NettoolMonitorHostInventory> inventories;

    public List<NettoolMonitorHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NettoolMonitorHostInventory> inventories) {
        this.inventories = inventories;
    }
}
