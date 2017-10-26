package com.syscxp.alarm.header.contact;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryContactGroupReply extends APIQueryReply {

    private List<ContactGroupInventory> inventories;

    public List<ContactGroupInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ContactGroupInventory> inventories) {
        this.inventories = inventories;
    }
}