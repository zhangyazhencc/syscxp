package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
@RestResponse(fieldsTo = {"inventories"})
public class APIQueryTunnelReply extends APIQueryReply {
    private List<TunnelInventory> inventories;

    public List<TunnelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelInventory> inventories) {
        this.inventories = inventories;
    }
}
