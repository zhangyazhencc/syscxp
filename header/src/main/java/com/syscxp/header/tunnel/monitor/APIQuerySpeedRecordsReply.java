package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
@RestResponse(allTo = "inventories")
public class APIQuerySpeedRecordsReply extends APIQueryReply {
    private List<SpeedRecordsInventory> inventories;

    public List<SpeedRecordsInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SpeedRecordsInventory> inventories) {
        this.inventories = inventories;
    }
}
