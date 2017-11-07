package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachPolicyByResourcesEvent extends APIEvent {

    private List<ResourceInventory> inventories;

    private long count;

    public APIAttachPolicyByResourcesEvent(String apiId) {super(apiId);}

    public APIAttachPolicyByResourcesEvent(){}

    public List<ResourceInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourceInventory> inventories) {
        this.inventories = inventories;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
