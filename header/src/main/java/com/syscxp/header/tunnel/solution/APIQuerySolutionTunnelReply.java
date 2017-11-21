package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by wangwg on 2017/11/21
 */

public class APIQuerySolutionTunnelReply extends APIQueryReply {
    private List<SolutionTunnelInventory> inventories;

    public List<SolutionTunnelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SolutionTunnelInventory> inventories) {
        this.inventories = inventories;
    }
}
