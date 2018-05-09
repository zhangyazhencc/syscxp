package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
public class APIListLSPTraceReply extends APIReply {
    private List<LSPTraceInventory> inventories;

    public List<LSPTraceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<LSPTraceInventory> inventories) {
        this.inventories = inventories;
    }
}
