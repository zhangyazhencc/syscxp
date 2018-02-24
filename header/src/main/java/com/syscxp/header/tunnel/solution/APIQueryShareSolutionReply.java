package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryShareSolutionReply extends APIQueryReply {
    private List<ShareSolutionInventory> inventories;

    public List<ShareSolutionInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ShareSolutionInventory> inventories) {
        this.inventories = inventories;
    }
}
