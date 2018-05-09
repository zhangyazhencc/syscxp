package com.syscxp.header.idc.solution;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/11/21
 */

public class APIQuerySolutionVpnReply extends APIQueryReply {
    private List<SolutionVpnInventory> inventories;

    public List<SolutionVpnInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SolutionVpnInventory> inventories) {
        this.inventories = inventories;
    }
}
