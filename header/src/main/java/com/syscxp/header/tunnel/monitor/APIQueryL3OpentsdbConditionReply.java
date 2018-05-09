package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-04-10.
 * @Description: .
 */
public class APIQueryL3OpentsdbConditionReply extends APIQueryReply {
    private L3ConditionInventory inventory;

    public L3ConditionInventory getInventory() {
        return inventory;
    }

    public void setInventory(L3ConditionInventory inventory) {
        this.inventory = inventory;
    }
}
