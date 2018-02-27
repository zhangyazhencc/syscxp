package com.syscxp.header.tunnel.solution;


import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIGetSolutionReply extends APIQueryReply {
    private SolutionInventory inventory;

    public SolutionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionInventory inventory) {
        this.inventory = inventory;
    }
}
