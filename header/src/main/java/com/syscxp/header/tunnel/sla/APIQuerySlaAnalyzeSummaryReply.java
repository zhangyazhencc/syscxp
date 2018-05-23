package com.syscxp.header.tunnel.sla;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.tunnel.monitor.SpeedRecordsInventory;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-22.
 * @Description: 查询SLA分析汇总.
 */

public class APIQuerySlaAnalyzeSummaryReply extends APIQueryReply {
    private List<SlaAnalyzeSummaryInventory> inventories;

    public List<SlaAnalyzeSummaryInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SlaAnalyzeSummaryInventory> inventories) {
        this.inventories = inventories;
    }
}
