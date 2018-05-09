package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
public class APIPreviewTETraceReply extends APIReply {
    private List<String> inventories;

    public List<String> getInventories() {
        return inventories;
    }

    public void setInventories(List<String> inventories) {
        this.inventories = inventories;
    }
}
