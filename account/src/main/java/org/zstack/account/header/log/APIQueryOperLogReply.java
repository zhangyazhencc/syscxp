package org.zstack.account.header.log;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryOperLogReply extends APIQueryReply {
    private List<OperLogInventory> inventories;

    public List<OperLogInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OperLogInventory> inventories) {
        this.inventories = inventories;
    }
}
