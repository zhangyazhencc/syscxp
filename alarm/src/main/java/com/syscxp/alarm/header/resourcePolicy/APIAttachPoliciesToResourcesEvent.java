package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachPoliciesToResourcesEvent extends APIEvent {


    public APIAttachPoliciesToResourcesEvent(String apiId) {super(apiId);}

    public APIAttachPoliciesToResourcesEvent(){}

    List<ResourceInventory> inventories;

    long count;

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
