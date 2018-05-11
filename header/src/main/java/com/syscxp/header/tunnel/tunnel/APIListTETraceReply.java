package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
public class APIListTETraceReply extends APIReply {
    private List<TETraceInventory> inventories;

    public List<TETraceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TETraceInventory> inventories) {
        this.inventories = inventories;
    }
}
