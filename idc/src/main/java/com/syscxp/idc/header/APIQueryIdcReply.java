package com.syscxp.idc.header;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryIdcReply extends APIQueryReply {

    List<IdcInventory> inventories;

    public List<IdcInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<IdcInventory> inventories) {
        this.inventories = inventories;
    }
}
