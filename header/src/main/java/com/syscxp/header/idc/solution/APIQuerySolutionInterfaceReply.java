package com.syscxp.header.idc.solution;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/11/21
 */

public class APIQuerySolutionInterfaceReply extends APIQueryReply {
    private List<SolutionInterfaceInventory> inventories;

    public List<SolutionInterfaceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SolutionInterfaceInventory> inventories) {
        this.inventories = inventories;
    }
}
