package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Create by DCY on 2017/11/20
 */
@RestResponse(fieldsTo = {"inventories"})
public class APIListCrossTunnelReply extends APIReply {
    private List<TunnelInventory> inventories;

    public List<TunnelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelInventory> inventories) {
        this.inventories = inventories;
    }
}
