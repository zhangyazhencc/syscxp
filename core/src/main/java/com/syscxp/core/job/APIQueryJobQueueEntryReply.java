package com.syscxp.core.job;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Create by DCY on 2017/12/13
 */
public class APIQueryJobQueueEntryReply extends APIQueryReply {
    private List<JobQueueEntryInventory> inventories;

    public List<JobQueueEntryInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<JobQueueEntryInventory> inventories) {
        this.inventories = inventories;
    }
}
