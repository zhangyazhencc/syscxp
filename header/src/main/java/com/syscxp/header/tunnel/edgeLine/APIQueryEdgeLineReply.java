package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Create by DCY on 2018/1/11
 */
@RestResponse(fieldsTo = {"inventories"})
public class APIQueryEdgeLineReply extends APIQueryReply {
    private List<EdgeLineInventory> inventories;

    public List<EdgeLineInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<EdgeLineInventory> inventories) {
        this.inventories = inventories;
    }
}
