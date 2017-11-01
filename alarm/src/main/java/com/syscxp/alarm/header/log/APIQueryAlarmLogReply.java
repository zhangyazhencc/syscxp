package com.syscxp.alarm.header.log;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryAlarmLogReply extends APIQueryReply{
    private List<AlarmLogInventory> inventories;

    public List<AlarmLogInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<AlarmLogInventory> inventories) {
        this.inventories = inventories;
    }
}
