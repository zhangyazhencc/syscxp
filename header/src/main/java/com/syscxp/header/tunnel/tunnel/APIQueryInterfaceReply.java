package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@RestResponse(fieldsTo = {"inventories"})
public class APIQueryInterfaceReply extends APIQueryReply {
    private List<InterfaceInventory> inventories;

    public List<InterfaceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<InterfaceInventory> inventories) {
        this.inventories = inventories;
    }
}
