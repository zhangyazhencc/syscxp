package com.syscxp.header.tunnel.sla;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-22.
 * @Description: 查询SLA分析数据.
 */

public class APIQuerySlaAnalyzeReply extends APIQueryReply {
    private List<SlaAnalyzeInventory> inventories;

    public List<SlaAnalyzeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SlaAnalyzeInventory> inventories) {
        this.inventories = inventories;
    }
}
