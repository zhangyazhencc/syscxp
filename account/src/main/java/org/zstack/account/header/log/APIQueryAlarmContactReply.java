package org.zstack.account.header.log;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

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
