package com.syscxp.header.idc.solution;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/11/21
 */

public class APIQuerySolutionReply extends APIQueryReply {
    private List<SolutionInventory> inventories;

    public List<SolutionInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SolutionInventory> inventories) {
        this.inventories = inventories;
    }
}