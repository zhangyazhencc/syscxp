package com.syscxp.header.tunnel.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2018/5/3
 */
public class APIQueryAccountNodeRefReply extends APIQueryReply {

    private List<AccountNodeRefInventory> inventories;

    public List<AccountNodeRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AccountNodeRefInventory> inventories) {
        this.inventories = inventories;
    }
}
