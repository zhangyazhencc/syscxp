package com.syscxp.account.header.log;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryAlarmContactReply extends APIQueryReply{
    private List<AlarmContactInventory> inventories;

    public List<AlarmContactInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AlarmContactInventory> inventories) {
        this.inventories = inventories;
    }
}
