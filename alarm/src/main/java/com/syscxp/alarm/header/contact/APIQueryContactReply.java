package com.syscxp.alarm.header.contact;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryContactReply  extends APIQueryReply {

    private List<ContactInventory> inventories;

    public List<ContactInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ContactInventory> inventories) {
        this.inventories = inventories;
    }
}
