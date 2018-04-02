package com.syscxp.idc.header;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryIdcDetailReply extends APIQueryReply {

    List<IdcDetailInventory> inventories;

    public List<IdcDetailInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<IdcDetailInventory> inventories) {
        this.inventories = inventories;
    }
}
