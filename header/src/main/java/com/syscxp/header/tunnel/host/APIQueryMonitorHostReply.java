package com.syscxp.header.tunnel.host;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryMonitorHostReply extends APIQueryReply {
    private List<MonitorHostInventory> inventories;

    public List<MonitorHostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<MonitorHostInventory> inventories) {
        this.inventories = inventories;
    }

}
