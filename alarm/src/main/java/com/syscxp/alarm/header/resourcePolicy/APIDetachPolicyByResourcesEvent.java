package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIDetachPolicyByResourcesEvent  extends APIEvent {

    private List<ResourceInventory> inventories;

    public APIDetachPolicyByResourcesEvent(String apiId) {
        super(apiId);
    }

    public APIDetachPolicyByResourcesEvent() {
    }

    public List<ResourceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourceInventory> inventories) {
        this.inventories = inventories;
    }
}